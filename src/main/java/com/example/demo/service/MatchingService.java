package com.example.demo.service;

import com.example.demo.dto.DonorMatchRequestDto;
import com.example.demo.dto.DonorMatchResultDto;
import com.example.demo.dto.MatchResultDto;
import com.example.demo.dto.PortfolioAllocationDto;
import com.example.demo.dto.PortfolioSuggestionDto;
import com.example.demo.dto.ProjectMatchRequestDto;
import com.example.demo.entity.Donor;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Project;
import com.example.demo.entity.VerificationStatus;
import com.example.demo.repository.DonorRepository;
import com.example.demo.repository.ProjectRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The matching engine. One scoring core, run in two directions:
 *  - {@link #findProjectsForDonor} : a donor declares criteria, gets projects.
 *  - {@link #findDonorsForProject} : an RLO describes a project, gets donors.
 *
 * Two principles from the hackathon brief are encoded directly here:
 *
 *  1. Equity by Design. The {@code EQUITY} dimension (the "anti-Matthew" layer)
 *     scores least-funded organizations highest, so among genuinely relevant
 *     matches the under-funded surface first instead of the already-popular.
 *
 *  2. Not a marketplace. The numeric score never leaves this class. Callers
 *     receive a qualitative fit label and a plain-language reason — fit and
 *     solidarity, not a performance ranking.
 *
 * Weights are named constants so they can be inspected and tuned transparently.
 */
@Service
public class MatchingService {

    // Donor -> Project weights (sum = 1.0)
    private static final double W_GEOGRAPHY = 0.30;
    private static final double W_FOCUS = 0.25;
    private static final double W_BUDGET = 0.20;
    private static final double W_EQUITY = 0.15; // anti-Matthew fairness tilt
    private static final double W_TRUST = 0.10;

    // RLO -> Donor weights (sum = 1.0); equity/trust don't apply to donors
    private static final double R_GEOGRAPHY = 0.40;
    private static final double R_FOCUS = 0.35;
    private static final double R_BUDGET = 0.25;

    // Results below this score are noise and are dropped.
    private static final double MIN_SCORE = 0.15;
    private static final int DEFAULT_LIMIT = 10;
    private static final int PORTFOLIO_SIZE = 4;

    private final ProjectRepository projectRepository;
    private final DonorRepository donorRepository;

    public MatchingService(ProjectRepository projectRepository, DonorRepository donorRepository) {
        this.projectRepository = projectRepository;
        this.donorRepository = donorRepository;
    }

    // ---------------------------------------------------------------------
    // Donor -> Projects
    // ---------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<MatchResultDto> findProjectsForDonor(DonorMatchRequestDto request) {
        List<Project> projects = projectRepository.findAll();
        double maxRecentFunding = maxRecentFunding(projects);
        boolean vouchedOnly = Boolean.TRUE.equals(request.getRequiresVouchedOnly());

        List<ScoredProject> scored = new ArrayList<>();
        for (Project project : projects) {
            Organization org = project.getOrganization();
            if (org == null) {
                continue;
            }
            // requiresVouchedOnly is a hard filter, not a soft penalty.
            if (vouchedOnly && !isVouched(org)) {
                continue;
            }

            double geo = scoreGeography(request.getPreferredRegions(), locationOf(project, org));
            double focus = scoreFocus(request.getPreferredEnergyFocus(), project.getEnergyFocusTags());
            double budget = scoreBudget(project.getTargetAmountEur(),
                    request.getMinGivingCapacityEur(), request.getMaxGivingCapacityEur());
            double equity = scoreEquity(org, maxRecentFunding);
            double trust = isVouched(org) ? 1.0 : 0.0;

            double score = geo * W_GEOGRAPHY
                    + focus * W_FOCUS
                    + budget * W_BUDGET
                    + equity * W_EQUITY
                    + trust * W_TRUST;

            if (score < MIN_SCORE) {
                continue;
            }
            scored.add(new ScoredProject(project, org, score,
                    projectReason(geo, focus, budget, equity, request, project, org)));
        }

        scored.sort(Comparator.comparingDouble((ScoredProject s) -> s.score).reversed());
        List<MatchResultDto> results = new ArrayList<>();
        for (ScoredProject s : scored.stream().limit(DEFAULT_LIMIT).toList()) {
            results.add(toMatchResult(s));
        }
        return results;
    }

    // ---------------------------------------------------------------------
    // RLO -> Donors
    // ---------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<DonorMatchResultDto> findDonorsForProject(ProjectMatchRequestDto request) {
        boolean projectVouched = Boolean.TRUE.equals(request.getIsVouched());
        List<ScoredDonor> scored = new ArrayList<>();

        for (Donor donor : donorRepository.findAll()) {
            // A donor who only funds vouched orgs is not reachable by an
            // unvouched project — respect that constraint from their side too.
            if (Boolean.TRUE.equals(donor.getRequiresVouchedOnly()) && !projectVouched) {
                continue;
            }

            double geo = scoreGeography(donor.getPreferredRegions(), request.getLocation());
            double focus = scoreFocus(donor.getPreferredEnergyFocus(), request.getEnergyFocusTags());
            double budget = scoreBudget(request.getTargetAmountEur(),
                    donor.getMinGivingCapacityEur(), donor.getMaxGivingCapacityEur());

            double score = geo * R_GEOGRAPHY + focus * R_FOCUS + budget * R_BUDGET;
            if (score < MIN_SCORE) {
                continue;
            }
            scored.add(new ScoredDonor(donor, score, donorReason(geo, focus, budget, donor, request)));
        }

        scored.sort(Comparator.comparingDouble((ScoredDonor s) -> s.score).reversed());
        List<DonorMatchResultDto> results = new ArrayList<>();
        for (ScoredDonor s : scored.stream().limit(DEFAULT_LIMIT).toList()) {
            results.add(toDonorResult(s));
        }
        return results;
    }

    // ---------------------------------------------------------------------
    // Portfolio: give without choosing one organization
    // ---------------------------------------------------------------------

    @Transactional(readOnly = true)
    public PortfolioSuggestionDto suggestPortfolio(DonorMatchRequestDto request, BigDecimal totalDonation) {
        List<MatchResultDto> top = findProjectsForDonor(request).stream()
                .limit(PORTFOLIO_SIZE)
                .toList();

        // Weight the split by remaining funding gap so the marginal euro closes
        // the most distance. Falls back to an even split if gaps are unknown.
        List<BigDecimal> gaps = new ArrayList<>();
        BigDecimal totalGap = BigDecimal.ZERO;
        for (MatchResultDto m : top) {
            BigDecimal target = m.getTargetAmountEur() == null ? BigDecimal.ZERO : m.getTargetAmountEur();
            BigDecimal current = m.getCurrentFundingAmountEur() == null ? BigDecimal.ZERO : m.getCurrentFundingAmountEur();
            BigDecimal gap = target.subtract(current).max(BigDecimal.ZERO);
            gaps.add(gap);
            totalGap = totalGap.add(gap);
        }

        List<PortfolioAllocationDto> allocations = new ArrayList<>();
        for (int i = 0; i < top.size(); i++) {
            MatchResultDto m = top.get(i);
            BigDecimal share;
            if (totalGap.signum() > 0) {
                share = totalDonation.multiply(gaps.get(i))
                        .divide(totalGap, 2, RoundingMode.HALF_UP);
            } else {
                share = totalDonation.divide(BigDecimal.valueOf(top.size()), 2, RoundingMode.HALF_UP);
            }
            allocations.add(PortfolioAllocationDto.builder()
                    .projectId(m.getProjectId())
                    .projectTitle(m.getProjectTitle())
                    .organizationName(m.getOrganizationName())
                    .displayLocation(m.getDisplayLocation())
                    .suggestedAmountEur(share)
                    .reason("Allocated by remaining funding gap to maximise direct impact")
                    .build());
        }

        String summary = String.format("Your €%s funds %d projects working on energy access%s",
                totalDonation.stripTrailingZeros().toPlainString(),
                allocations.size(),
                regionSuffix(request.getPreferredRegions()));

        return PortfolioSuggestionDto.builder()
                .totalDonationEur(totalDonation)
                .allocations(allocations)
                .summary(summary)
                .build();
    }

    // ---------------------------------------------------------------------
    // Shared dimension scorers (0.0 – 1.0)
    // ---------------------------------------------------------------------

    /** Simple contains: a preferred region matching anywhere in the location string. */
    private double scoreGeography(List<String> preferredRegions, String location) {
        if (preferredRegions == null || preferredRegions.isEmpty() || location == null || location.isBlank()) {
            return 0.0;
        }
        String haystack = location.toLowerCase();
        for (String region : preferredRegions) {
            if (region != null && !region.isBlank() && haystack.contains(region.toLowerCase().trim())) {
                return 1.0;
            }
        }
        return 0.0;
    }

    /** Fraction of the requester's preferred focus areas covered by the other side's tags. */
    private double scoreFocus(List<String> preferred, List<String> tags) {
        if (preferred == null || preferred.isEmpty() || tags == null || tags.isEmpty()) {
            return 0.0;
        }
        long hits = preferred.stream()
                .filter(p -> p != null && tags.stream()
                        .anyMatch(t -> t != null && t.equalsIgnoreCase(p.trim())))
                .count();
        return (double) hits / preferred.size();
    }

    /** Does the funding target sit inside the donor's giving range? */
    private double scoreBudget(BigDecimal target, BigDecimal min, BigDecimal max) {
        if (target == null) {
            return 0.0;
        }
        boolean aboveMin = min == null || target.compareTo(min) >= 0;
        boolean belowMax = max == null || target.compareTo(max) <= 0;
        if (aboveMin && belowMax) {
            return 1.0;
        }
        if (max != null && target.compareTo(max) > 0) {
            return 0.6; // needs more than this donor gives — still partly fundable
        }
        return 0.3; // needs less than the donor's floor — risk of over-funding
    }

    /** Anti-Matthew: the less an organization has recently received, the higher it scores. */
    private double scoreEquity(Organization org, double maxRecentFunding) {
        if (maxRecentFunding <= 0) {
            return 1.0;
        }
        double recent = org.getRecentFundingReceivedEur() == null ? 0.0 : org.getRecentFundingReceivedEur();
        double equity = 1.0 - (recent / maxRecentFunding);
        return Math.max(0.0, Math.min(1.0, equity));
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private double maxRecentFunding(List<Project> projects) {
        double max = 0.0;
        for (Project p : projects) {
            Organization org = p.getOrganization();
            if (org != null && org.getRecentFundingReceivedEur() != null) {
                max = Math.max(max, org.getRecentFundingReceivedEur());
            }
        }
        return max;
    }

    private boolean isVouched(Organization org) {
        VerificationStatus status = org.getVerificationStatus();
        return status == VerificationStatus.VOUCHED || status == VerificationStatus.FULLY_VERIFIED;
    }

    private String locationOf(Project project, Organization org) {
        if (project.getDisplayLocation() != null && !project.getDisplayLocation().isBlank()) {
            return project.getDisplayLocation();
        }
        return org.getHqLocation();
    }

    /** Qualitative label — never the raw number. */
    private String fitLabel(double score) {
        if (score >= 0.70) {
            return "Strong fit";
        }
        if (score >= 0.45) {
            return "Good fit";
        }
        return "Complementary fit";
    }

    private String projectReason(double geo, double focus, double budget, double equity,
            DonorMatchRequestDto request, Project project, Organization org) {
        List<String> parts = new ArrayList<>();
        if (geo > 0) {
            parts.add("works in a region you care about (" + locationOf(project, org) + ")");
        }
        if (focus > 0) {
            parts.add("focused on " + String.join(", ", project.getEnergyFocusTags()));
        }
        if (budget >= 1.0) {
            parts.add("funding need fits your giving range");
        }
        if (equity >= 0.6) {
            parts.add("currently under-funded relative to peers");
        }
        if (isVouched(org) && !org.getVouchedBy().isEmpty()) {
            parts.add("vouched for by " + String.join(", ", org.getVouchedBy()));
        }
        return parts.isEmpty() ? "Complementary to your interests" : capitalize(String.join("; ", parts));
    }

    private String donorReason(double geo, double focus, double budget, Donor donor, ProjectMatchRequestDto request) {
        List<String> parts = new ArrayList<>();
        if (geo > 0) {
            parts.add("gives in your region");
        }
        if (focus > 0) {
            parts.add("funds your focus area");
        }
        if (budget >= 1.0) {
            parts.add("your need fits their giving range");
        }
        return parts.isEmpty() ? "Open to complementary projects" : capitalize(String.join("; ", parts));
    }

    private String regionSuffix(List<String> regions) {
        if (regions == null || regions.isEmpty()) {
            return "";
        }
        return " across " + String.join(", ", regions);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private MatchResultDto toMatchResult(ScoredProject s) {
        return MatchResultDto.builder()
                .projectId(s.project.getId())
                .projectTitle(s.project.getTitle())
                .organizationId(s.org.getId())
                .organizationName(s.org.getName())
                .oneSentenceMission(s.org.getOneSentenceMission())
                .displayLocation(locationOf(s.project, s.org))
                .energyFocusTags(s.project.getEnergyFocusTags())
                .targetAmountEur(s.project.getTargetAmountEur())
                .currentFundingAmountEur(s.project.getCurrentFundingAmountEur())
                .verificationStatus(s.org.getVerificationStatus())
                .vouchedBy(s.org.getVouchedBy())
                .fitLabel(fitLabel(s.score))
                .matchReason(s.reason)
                .build();
    }

    private DonorMatchResultDto toDonorResult(ScoredDonor s) {
        return DonorMatchResultDto.builder()
                .donorId(s.donor.getId())
                .donorName(s.donor.getName())
                .donorType(s.donor.getDonorType())
                .preferredRegions(s.donor.getPreferredRegions())
                .preferredEnergyFocus(s.donor.getPreferredEnergyFocus())
                .minGivingCapacityEur(s.donor.getMinGivingCapacityEur())
                .maxGivingCapacityEur(s.donor.getMaxGivingCapacityEur())
                .fitLabel(fitLabel(s.score))
                .matchReason(s.reason)
                .build();
    }

    // Internal carriers so the score stays inside the service and never reaches a DTO.
    private record ScoredProject(Project project, Organization org, double score, String reason) {
    }

    private record ScoredDonor(Donor donor, double score, String reason) {
    }
}
