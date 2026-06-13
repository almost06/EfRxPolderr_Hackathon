package com.example.demo.controller;

import com.example.demo.dto.DashboardSummaryDto;
import com.example.demo.dto.DirectDonationRequestDto;
import com.example.demo.dto.PortfolioDto;
import com.example.demo.dto.ProjectDto;
import com.example.demo.entity.Donor;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Portfolio;
import com.example.demo.entity.Project;
import com.example.demo.mapper.PortfolioMapper;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.DonorRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.PortfolioRepository;
import com.example.demo.repository.ProjectRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DonorRepository donorRepository;
    private final OrganizationRepository organizationRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProjectRepository projectRepository;

    public DashboardController(
            DonorRepository donorRepository,
            OrganizationRepository organizationRepository,
            PortfolioRepository portfolioRepository,
            ProjectRepository projectRepository) {
        this.donorRepository = donorRepository;
        this.organizationRepository = organizationRepository;
        this.portfolioRepository = portfolioRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDto> getDashboard(
            @RequestParam Long accountId,
            @RequestParam String role) {
        if ("DONOR".equalsIgnoreCase(role)) {
            return donorRepository.findById(accountId)
                    .map(this::toDonorSummary)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        if ("ORGANIZATION".equalsIgnoreCase(role)) {
            return organizationRepository.findById(accountId)
                    .map(this::toOrganizationSummary)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/portfolios")
    public List<PortfolioDto> getPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(PortfolioMapper::toDto)
                .toList();
    }

    @GetMapping("/projects")
    public List<ProjectDto> getProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectMapper::toDto)
                .toList();
    }

    @Transactional
    @PostMapping("/donations/direct")
    public ResponseEntity<ProjectDto> directDonation(@RequestBody DirectDonationRequestDto request) {
        if (request == null || request.getAccountId() == null || request.getProjectId() == null
                || request.getAmountEur() == null || request.getAmountEur().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }

        Project project = projectRepository.findById(request.getProjectId()).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        if (!addDonationToAccount(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BigDecimal amount = request.getAmountEur();
        project.setCurrentFundingAmountEur(defaultMoney(project.getCurrentFundingAmountEur()).add(amount));

        Portfolio portfolio = project.getPortfolio();
        if (portfolio != null) {
            portfolio.setCurrentFundingAmountEur(defaultMoney(portfolio.getCurrentFundingAmountEur()).add(amount));
            if (portfolio.getTargetAmountEur() != null) {
                portfolio.setIsFullyFunded(portfolio.getCurrentFundingAmountEur().compareTo(portfolio.getTargetAmountEur()) >= 0);
            }
            portfolioRepository.save(portfolio);
        }

        Project savedProject = projectRepository.save(project);
        return ResponseEntity.ok(ProjectMapper.toDto(savedProject));
    }

    private DashboardSummaryDto toDonorSummary(Donor donor) {
        return DashboardSummaryDto.builder()
                .id(donor.getId())
                .name(donor.getName())
                .role("DONOR")
                .email(donor.getEmail())
                .totalDonatedEur(defaultMoney(donor.getTotalDonatedEur()))
                .totalReceivedEur(BigDecimal.ZERO)
                .build();
    }

    private DashboardSummaryDto toOrganizationSummary(Organization organization) {
        return DashboardSummaryDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .role("ORGANIZATION")
                .email(organization.getContactEmail())
                .totalDonatedEur(defaultMoney(organization.getTotalDonatedEur()))
                .totalReceivedEur(calculateReceived(organization.getId()))
                .build();
    }

    private BigDecimal calculateReceived(Long organizationId) {
        return projectRepository.findByOrganizationId(organizationId).stream()
                .map(Project::getCurrentFundingAmountEur)
                .map(this::defaultMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean addDonationToAccount(DirectDonationRequestDto request) {
        BigDecimal amount = request.getAmountEur();

        if ("DONOR".equalsIgnoreCase(request.getRole())) {
            return donorRepository.findById(request.getAccountId())
                    .map(donor -> {
                        donor.setTotalDonatedEur(defaultMoney(donor.getTotalDonatedEur()).add(amount));
                        donorRepository.save(donor);
                        return true;
                    })
                    .orElse(false);
        }

        if ("ORGANIZATION".equalsIgnoreCase(request.getRole())) {
            return organizationRepository.findById(request.getAccountId())
                    .map(organization -> {
                        organization.setTotalDonatedEur(defaultMoney(organization.getTotalDonatedEur()).add(amount));
                        organizationRepository.save(organization);
                        return true;
                    })
                    .orElse(false);
        }

        return false;
    }

    private BigDecimal defaultMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
