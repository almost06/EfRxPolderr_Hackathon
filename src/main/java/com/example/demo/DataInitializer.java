package com.example.demo;

import com.example.demo.entity.Donor;
import com.example.demo.entity.DonorType;
import com.example.demo.entity.Organization;
import com.example.demo.entity.OrganizationType;
import com.example.demo.entity.Portfolio;
import com.example.demo.entity.Project;
import com.example.demo.entity.VerificationStatus;
import com.example.demo.repository.DonorRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.PortfolioRepository;
import com.example.demo.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the H2 in-memory database with realistic demo data on every startup.
 *
 * Organizations are drawn from EfR's actual project portfolio (Kakuma, La Guajira,
 * DR Congo) plus plausible peers. recentFundingReceivedEur varies widely so the
 * Anti-Matthew equity layer produces visible differences in ranking during the demo.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository orgRepo;
    private final ProjectRepository projectRepo;
    private final DonorRepository donorRepo;
    private final PortfolioRepository portfolioRepo;

    public DataInitializer(OrganizationRepository orgRepo,
                           ProjectRepository projectRepo,
                           DonorRepository donorRepo,
                           PortfolioRepository portfolioRepo) {
        this.orgRepo = orgRepo;
        this.projectRepo = projectRepo;
        this.donorRepo = donorRepo;
        this.portfolioRepo = portfolioRepo;
    }

    @Override
    public void run(String... args) {
        seedOrganizationsAndProjects();
        seedDonors();
        seedPortfolios();
    }

    // ------------------------------------------------------------------
    // Organizations + Projects
    // ------------------------------------------------------------------

    private void seedOrganizationsAndProjects() {

        // 1. Restore Hope Kakuma --- EfR real project, Kenya
        Organization kakuma = org(
            "Restore Hope Kakuma",
            "Bringing solar electricity to 22,000 refugees in Kakuma camp, Kenya.",
            "David Omondi", "david@restorehopekakuma.org", "+254700123456",
            "Kakuma, Turkana County, Kenya",
            OrganizationType.RLO, VerificationStatus.FULLY_VERIFIED,
            List.of("UNHCR", "Energy for Refugees"),
            45_000.0
        );
        project(kakuma,
            "Kakuma Solar Grid - Phase 2",
            "Phase 2 expands the micro-grid to Block C and D, adding 120 kWp of " +
            "solar panels and battery storage to power 1,200 additional households.",
            List.of("solar", "refugees", "micro-grid"),
            new BigDecimal("30000"), new BigDecimal("8400"),
            "Kakuma, Kenya",
            LocalDate.of(2026, 9, 30)
        );

        // 2. Wayuu Solar Initiative --- EfR real project, Colombia
        Organization wayuu = org(
            "Wayuu Solar Initiative",
            "Off-grid solar for indigenous Wayuu communities in La Guajira, Colombia.",
            "Carmen Ipuana", "carmen@wayuusolar.org", "+573001234567",
            "Riohacha, La Guajira, Colombia",
            OrganizationType.RLO, VerificationStatus.VOUCHED,
            List.of("Polderr Network"),
            12_000.0
        );
        project(wayuu,
            "La Guajira Off-Grid Solar Kits",
            "Deploying 80 standalone solar home systems to Wayuu rancherias cut off " +
            "from the national grid, providing lighting and phone-charging.",
            List.of("solar", "indigenous", "off-grid"),
            new BigDecimal("18000"), new BigDecimal("5200"),
            "La Guajira, Colombia",
            LocalDate.of(2026, 8, 15)
        );

        // 3. Beyond Blackouts Clinic --- EfR real project, DR Congo
        Organization drc = org(
            "Beyond Blackouts Clinic",
            "Solar-powered clinic serving 8,000 displaced people in eastern DR Congo.",
            "Dr. Celeste Nzangi", "celeste@beyondblackouts.org", "+243812345678",
            "Bunia, Ituri Province, DR Congo",
            OrganizationType.RLO, VerificationStatus.FULLY_VERIFIED,
            List.of("Medecins Sans Frontieres", "Energy for Refugees"),
            28_000.0
        );
        project(drc,
            "Clinic Solar Resilience - DRC",
            "Installing a 30 kWp rooftop solar system and 48-hour battery backup " +
            "so the clinic can operate vaccines, surgery lighting, and oxygen through " +
            "grid outages.",
            List.of("solar", "healthcare", "displacement"),
            new BigDecimal("25000"), new BigDecimal("9100"),
            "Ituri Province, DR Congo",
            LocalDate.of(2026, 10, 1)
        );

        // 4. NARA Climate RLO --- South Sudan, newly formed, no funding yet
        Organization nara = org(
            "NARA Climate RLO",
            "Refugee-led climate adaptation and clean cooking in South Sudan.",
            "Amara Deng", "amara@naraclimate.org", "+211912345678",
            "Juba, South Sudan",
            OrganizationType.RLO, VerificationStatus.UNVERIFIED,
            List.of(),
            3_000.0
        );
        project(nara,
            "South Sudan Cookstove Programme",
            "Distributing 500 improved biomass cookstoves to reduce indoor air " +
            "pollution and deforestation pressure around Juba IDP camps.",
            List.of("cookstoves", "climate", "displacement"),
            new BigDecimal("12000"), new BigDecimal("1100"),
            "Juba, South Sudan",
            LocalDate.of(2026, 12, 31)
        );

        // 5. Gaza Energy Recovery --- Palestine, vouched
        Organization gaza = org(
            "Gaza Energy Recovery",
            "Emergency solar installations restoring power to clinics and water pumps in Gaza.",
            "Yusuf Al-Masri", "yusuf@gazaenergy.org", "+970591234567",
            "Gaza City, Palestine",
            OrganizationType.RLO, VerificationStatus.VOUCHED,
            List.of("Islamic Relief"),
            8_000.0
        );
        project(gaza,
            "Gaza Rooftop Solar Emergency",
            "Rapid deployment of rooftop solar on 12 health facilities and 4 water " +
            "desalination stations in northern Gaza to restore essential services.",
            List.of("solar", "emergency", "healthcare"),
            new BigDecimal("22000"), new BigDecimal("4300"),
            "Gaza, Palestine",
            LocalDate.of(2026, 7, 1)
        );

        // 6. Sahel Sunlight Collective --- Niger, early-stage, minimal funding
        Organization sahel = org(
            "Sahel Sunlight Collective",
            "Bringing affordable solar lanterns and phone-charging to off-grid villages in Niger.",
            "Fatima Moussa", "fatima@sahelsunlight.org", "+22796123456",
            "Agadez, Niger",
            OrganizationType.RLO, VerificationStatus.UNVERIFIED,
            List.of(),
            2_000.0
        );
        project(sahel,
            "Nigerien Village Solar Kits",
            "Bulk procurement and local distribution of 300 solar home systems for " +
            "villages along the Niger-Libya corridor, serving families with no grid access.",
            List.of("solar", "rural", "off-grid"),
            new BigDecimal("8000"), new BigDecimal("500"),
            "Agadez, Niger",
            LocalDate.of(2026, 11, 30)
        );

        // 7. Mekong Clean Fuel Alliance --- Laos, mid-funded
        Organization mekong = org(
            "Mekong Clean Fuel Alliance",
            "Replacing open-fire cooking with biomass gasifiers in riverine communities along the Mekong.",
            "Bounsong Phommasack", "bounsong@mekongcleanfuel.org", "+85620123456",
            "Vientiane, Laos",
            OrganizationType.RLO, VerificationStatus.VOUCHED,
            List.of("GIZ"),
            15_000.0
        );
        project(mekong,
            "Mekong Biomass Fuel Switch",
            "Installing 200 household biomass gasifiers and training local women as " +
            "maintenance technicians in three Laotian provinces.",
            List.of("biomass", "cookstoves", "rural"),
            new BigDecimal("15000"), new BigDecimal("6200"),
            "Mekong River Basin, Laos",
            LocalDate.of(2026, 9, 1)
        );

        // 8. Rohingya Renewable Network --- Bangladesh, highest recent funding (Anti-Matthew demo)
        Organization rohingya = org(
            "Rohingya Renewable Network",
            "Community-owned solar micro-grids inside Cox's Bazar refugee camps, Bangladesh.",
            "Mohammad Alam", "mohammad@rohingyarenewable.org", "+8801812345678",
            "Cox's Bazar, Bangladesh",
            OrganizationType.RLO, VerificationStatus.FULLY_VERIFIED,
            List.of("UNHCR", "IRC"),
            55_000.0  // highest recent funding --- penalised by Anti-Matthew
        );
        project(rohingya,
            "Cox's Bazar Camp Solar Micro-grid",
            "A 150 kWp community solar plant with pre-paid meters serving 3,000 " +
            "Rohingya households in blocks 11-14, Cox's Bazar.",
            List.of("solar", "refugees", "micro-grid"),
            new BigDecimal("35000"), new BigDecimal("18000"),
            "Cox's Bazar, Bangladesh",
            LocalDate.of(2026, 8, 31)
        );

        // 9. Andean Wind Initiative --- Peru, early-stage
        Organization andean = org(
            "Andean Wind Initiative",
            "Small wind turbines powering indigenous highland communities in the Peruvian Andes.",
            "Rosa Quispe", "rosa@andeanwind.org", "+51912345678",
            "Puno, Peru",
            OrganizationType.RLO, VerificationStatus.UNVERIFIED,
            List.of(),
            4_500.0
        );
        project(andean,
            "Andean Highland Wind Turbines",
            "Deploying 15 micro-wind turbines (5 kW each) at altitudes above 4,000 m " +
            "where solar irradiance is insufficient, serving Quechua communities.",
            List.of("wind", "indigenous", "off-grid"),
            new BigDecimal("20000"), new BigDecimal("3000"),
            "Puno Region, Peru",
            LocalDate.of(2026, 12, 15)
        );

        // 10. Bright Clinics International --- NGO partner, East Africa
        Organization brightClinics = org(
            "Bright Clinics International",
            "Equipping rural clinics with solar refrigeration, lighting, and backup power.",
            "Naomi Becker", "naomi@brightclinics.org", "+254711222333",
            "Nairobi, Kenya",
            OrganizationType.NGO, VerificationStatus.FULLY_VERIFIED,
            List.of("WHO", "Energy for Refugees"),
            38_000.0
        );
        project(brightClinics,
            "Solar Vaccine Fridges for Rural Clinics",
            "Installing solar vaccine refrigeration and efficient LED lighting in 18 rural clinics that currently lose cold-chain capacity during blackouts.",
            List.of("solar", "healthcare", "rural"),
            new BigDecimal("28000"), new BigDecimal("7400"),
            "Western Kenya",
            LocalDate.of(2026, 10, 20)
        );
        project(brightClinics,
            "Clinic Battery Backup Kits",
            "Supplying modular battery backup kits for maternity wards and emergency rooms serving remote communities.",
            List.of("solar", "healthcare", "battery"),
            new BigDecimal("16000"), new BigDecimal("2600"),
            "Kisumu County, Kenya",
            LocalDate.of(2026, 11, 10)
        );

        // 11. GridBridge NGO --- NGO partner, regional infrastructure
        Organization gridBridge = org(
            "GridBridge NGO",
            "Building finance-ready clean-energy projects with local community operators.",
            "Elias Hart", "elias@gridbridge.org", "+31201234567",
            "Amsterdam, Netherlands",
            OrganizationType.NGO, VerificationStatus.VOUCHED,
            List.of("GIZ", "Dutch Relief Alliance"),
            22_000.0
        );
        project(gridBridge,
            "Community Mini-Grid Feasibility Fund",
            "Funding site surveys, load analysis, and community operator training for five mini-grid locations in displacement-affected regions.",
            List.of("solar", "micro-grid", "finance"),
            new BigDecimal("24000"), new BigDecimal("4800"),
            "Kenya, Uganda, and Rwanda",
            LocalDate.of(2026, 9, 25)
        );
    }

    // ------------------------------------------------------------------
    // Donors
    // ------------------------------------------------------------------

    private void seedDonors() {

        // 1. Large institutional funder --- Africa focus, solar+wind, open to all
        donor("Shell Foundation", "grants@shellfoundation.org",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "East Africa", "Kenya"),
            List.of("solar", "wind", "off-grid"),
            new BigDecimal("20000"), new BigDecimal("100000"),
            false
        );

        // 2. UNHCR-style institutional funder --- refugee camps, vouched orgs only
        donor("UNHCR Innovation Fund", "innovation@unhcr.org",
            DonorType.INSTITUTIONAL,
            List.of("East Africa", "Kenya", "Bangladesh", "Palestine"),
            List.of("solar", "refugees", "micro-grid"),
            new BigDecimal("10000"), new BigDecimal("50000"),
            true
        );

        // 3. Dutch foundation --- broad geography, cookstoves + solar
        donor("EnergieVan Morgen Fonds", "info@energievanmorgen.nl",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "Asia", "Laos", "Niger"),
            List.of("solar", "cookstoves", "biomass"),
            new BigDecimal("5000"), new BigDecimal("30000"),
            false
        );

        // 4. Individual impact investor --- Latin America + Africa, indigenous communities
        donor("Maria Santos", "maria.santos@impact.org",
            DonorType.INDIVIDUAL,
            List.of("Latin America", "Colombia", "Peru", "Africa"),
            List.of("solar", "indigenous", "off-grid"),
            new BigDecimal("2000"), new BigDecimal("15000"),
            false
        );

        // 5. Pan-sector climate tech fund --- global, all energy types, large tickets
        donor("ClimateTech Impact Fund", "apply@climatetechfund.org",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "Asia", "Latin America", "Middle East"),
            List.of("solar", "wind", "biomass", "cookstoves", "micro-grid"),
            new BigDecimal("15000"), new BigDecimal("80000"),
            false
        );

        // 6. Dutch lottery-funded charity --- Africa, healthcare + solar, vouched only
        donor("Postcode Loterij Fonds", "aanvragen@postcodeloterij.nl",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "Congo", "Kenya", "South Sudan"),
            List.of("solar", "healthcare", "displacement"),
            new BigDecimal("10000"), new BigDecimal("40000"),
            true
        );

        // 7. Individual diaspora donor --- Gaza / Palestine focus
        donor("Ahmed Al-Masri", "ahmed.almasri@gmail.com",
            DonorType.INDIVIDUAL,
            List.of("Palestine", "Gaza", "Middle East"),
            List.of("solar", "emergency", "healthcare"),
            new BigDecimal("1000"), new BigDecimal("8000"),
            false
        );

        // 8. Corporate donor --- practical engineering and finance support
        donor("VoltWorks Corporate Giving", "giving@voltworks.example",
            DonorType.CORPORATE,
            List.of("Africa", "Latin America", "Asia"),
            List.of("solar", "micro-grid", "battery"),
            new BigDecimal("5000"), new BigDecimal("25000"),
            false
        );

        // 9. Individual donor --- small direct donations, clinic focus
        donor("Lena Fischer", "lena.fischer@example.com",
            DonorType.INDIVIDUAL,
            List.of("Africa", "Kenya", "Congo"),
            List.of("solar", "healthcare"),
            new BigDecimal("250"), new BigDecimal("5000"),
            false
        );
    }

    private void seedPortfolios() {
        portfolio(
            "Refugee Energy Access Portfolio",
            "A blended portfolio of solar and micro-grid projects serving refugee-led and displacement-affected communities.",
            List.of(
                "Kakuma Solar Grid - Phase 2",
                "Cox's Bazar Camp Solar Micro-grid",
                "Gaza Rooftop Solar Emergency",
                "Community Mini-Grid Feasibility Fund"
            )
        );

        portfolio(
            "Clinics and Community Resilience Portfolio",
            "Healthcare, clean cooking, and rural resilience projects where direct donations can unlock immediate operating capacity.",
            List.of(
                "Clinic Solar Resilience - DRC",
                "Solar Vaccine Fridges for Rural Clinics",
                "Clinic Battery Backup Kits",
                "South Sudan Cookstove Programme",
                "Mekong Biomass Fuel Switch"
            )
        );

        portfolio(
            "Indigenous and Off-Grid Innovation Portfolio",
            "Early-stage clean energy projects for indigenous and remote communities outside conventional funding channels.",
            List.of(
                "La Guajira Off-Grid Solar Kits",
                "Nigerien Village Solar Kits",
                "Andean Highland Wind Turbines"
            )
        );
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Organization org(String name, String mission, String contactName,
            String contactEmail, String contactWhatsapp, String hqLocation,
            OrganizationType orgType, VerificationStatus status,
            List<String> vouchedBy, double recentFunding) {
        Organization o = orgRepo.findByContactEmailIgnoreCase(contactEmail).orElseGet(Organization::new);
        o.setName(name);
        o.setOneSentenceMission(mission);
        o.setDescription(mission);
        o.setContactName(contactName);
        o.setContactEmail(contactEmail);
        o.setContactWhatsapp(contactWhatsapp);
        o.setHqLocation(hqLocation);
        o.setOrganizationType(orgType);
        o.setVerificationStatus(status);
        o.setVouchedBy(vouchedBy);
        o.setRecentFundingReceivedEur(recentFunding);
        if (o.getTotalDonatedEur() == null) {
            o.setTotalDonatedEur(BigDecimal.ZERO);
        }
        return orgRepo.save(o);
    }

    private void portfolio(String title, String description, List<String> projectTitles) {
        Portfolio portfolio = portfolioRepo.findByTitleIgnoreCase(title).orElseGet(Portfolio::new);
        portfolio.setTitle(title);
        portfolio.setDescription(description);

        List<Project> projects = new ArrayList<>();
        Set<String> seenTitles = new HashSet<>();
        projectRepo.findAll().stream()
                .filter(project -> projectTitles.stream()
                        .anyMatch(projectTitle -> projectTitle.equalsIgnoreCase(project.getTitle())))
                .forEach(project -> {
                    String key = project.getTitle().toLowerCase();
                    if (seenTitles.add(key)) {
                        projects.add(project);
                    }
                });

        BigDecimal target = projects.stream()
                .map(Project::getTargetAmountEur)
                .map(this::money)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal current = projects.stream()
                .map(Project::getCurrentFundingAmountEur)
                .map(this::money)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        portfolio.setTargetAmountEur(target);
        portfolio.setCurrentFundingAmountEur(current);
        portfolio.setIsFullyFunded(target.compareTo(BigDecimal.ZERO) > 0 && current.compareTo(target) >= 0);
        Portfolio savedPortfolio = portfolioRepo.save(portfolio);

        projects.forEach(project -> {
            project.setPortfolio(savedPortfolio);
            projectRepo.save(project);
        });
    }

    private void project(Organization org, String title, String description,
            List<String> tags, BigDecimal target, BigDecimal current,
            String displayLocation, LocalDate deadline) {
        Project p = projectRepo.findByOrganizationId(org.getId()).stream()
                .filter(existing -> title.equalsIgnoreCase(existing.getTitle()))
                .findFirst()
                .orElseGet(Project::new);
        p.setOrganization(org);
        p.setTitle(title);
        p.setAiPolishedDescription(description);
        p.setRawInputWhatsapp(description); // raw = polished for seed data
        p.setEnergyFocusTags(tags);
        p.setSkillsNeededTags(skillTagsFor(tags));
        p.setTargetAmountEur(target);
        p.setCurrentFundingAmountEur(current);
        p.setDisplayLocation(displayLocation);
        p.setFundingDeadline(deadline);
        p.setIsUnlocked(true);
        projectRepo.save(p);
    }

    private void donor(String name, String email, DonorType type, List<String> regions,
            List<String> focus, BigDecimal min, BigDecimal max,
            boolean requiresVouched) {
        Donor d = donorRepo.findByEmailIgnoreCase(email).orElseGet(Donor::new);
        d.setName(name);
        d.setEmail(email);
        d.setDonorType(type);
        d.setPreferredRegions(regions);
        d.setPreferredEnergyFocus(focus);
        d.setVolunteerSkills(volunteerSkillsFor(focus));
        d.setMinGivingCapacityEur(min);
        d.setMaxGivingCapacityEur(max);
        if (d.getTotalDonatedEur() == null) {
            d.setTotalDonatedEur(BigDecimal.ZERO);
        }
        d.setRequiresVouchedOnly(requiresVouched);
        donorRepo.save(d);
    }

    private BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private List<String> skillTagsFor(List<String> tags) {
        if (tags.contains("healthcare")) {
            return List.of("ELECTRICAL_ENGINEERING", "MEDICAL_EQUIPMENT", "SOLAR_INSTALLATION");
        }
        if (tags.contains("micro-grid")) {
            return List.of("ELECTRICAL_ENGINEERING", "PROJECT_FINANCE", "COMMUNITY_TRAINING");
        }
        if (tags.contains("cookstoves") || tags.contains("biomass")) {
            return List.of("MECHANICAL_ENGINEERING", "SUPPLY_CHAIN", "COMMUNITY_TRAINING");
        }
        if (tags.contains("wind")) {
            return List.of("ELECTRICAL_ENGINEERING", "MECHANICAL_ENGINEERING", "MAINTENANCE_TRAINING");
        }
        return List.of("SOLAR_INSTALLATION", "ACCOUNTING", "COMMUNITY_TRAINING");
    }

    private List<String> volunteerSkillsFor(List<String> focus) {
        if (focus.contains("healthcare")) {
            return List.of("MEDICAL_EQUIPMENT", "LEGAL", "ACCOUNTING");
        }
        if (focus.contains("micro-grid")) {
            return List.of("ELECTRICAL_ENGINEERING", "PROJECT_FINANCE", "LEGAL");
        }
        if (focus.contains("cookstoves") || focus.contains("biomass")) {
            return List.of("SUPPLY_CHAIN", "MECHANICAL_ENGINEERING", "ACCOUNTING");
        }
        return List.of("LEGAL", "ACCOUNTING", "SOLAR_INSTALLATION");
    }
}
