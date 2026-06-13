package com.example.demo;

import com.example.demo.entity.Donor;
import com.example.demo.entity.DonorType;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Project;
import com.example.demo.entity.VerificationStatus;
import com.example.demo.repository.DonorRepository;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.ProjectRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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

    public DataInitializer(OrganizationRepository orgRepo,
                           ProjectRepository projectRepo,
                           DonorRepository donorRepo) {
        this.orgRepo = orgRepo;
        this.projectRepo = projectRepo;
        this.donorRepo = donorRepo;
    }

    @Override
    public void run(String... args) {
        seedOrganizationsAndProjects();
        seedDonors();
    }

    // ------------------------------------------------------------------
    // Organizations + Projects
    // ------------------------------------------------------------------

    private void seedOrganizationsAndProjects() {

        // 1. Restore Hope Kakuma — EfR real project, Kenya
        Organization kakuma = org(
            "Restore Hope Kakuma",
            "Bringing solar electricity to 22,000 refugees in Kakuma camp, Kenya.",
            "David Omondi", "+254700123456",
            "Kakuma, Turkana County, Kenya",
            VerificationStatus.FULLY_VERIFIED,
            List.of("UNHCR", "Energy for Refugees"),
            45_000.0
        );
        project(kakuma,
            "Kakuma Solar Grid — Phase 2",
            "Phase 2 expands the micro-grid to Block C and D, adding 120 kWp of " +
            "solar panels and battery storage to power 1,200 additional households.",
            List.of("solar", "refugees", "micro-grid"),
            new BigDecimal("30000"), new BigDecimal("8400"),
            "Kakuma, Kenya",
            LocalDate.of(2026, 9, 30)
        );

        // 2. Wayuu Solar Initiative — EfR real project, Colombia
        Organization wayuu = org(
            "Wayuu Solar Initiative",
            "Off-grid solar for indigenous Wayuu communities in La Guajira, Colombia.",
            "Carmen Ipuana", "+573001234567",
            "Riohacha, La Guajira, Colombia",
            VerificationStatus.VOUCHED,
            List.of("Polderr Network"),
            12_000.0
        );
        project(wayuu,
            "La Guajira Off-Grid Solar Kits",
            "Deploying 80 standalone solar home systems to Wayuu rancherías cut off " +
            "from the national grid, providing lighting and phone-charging.",
            List.of("solar", "indigenous", "off-grid"),
            new BigDecimal("18000"), new BigDecimal("5200"),
            "La Guajira, Colombia",
            LocalDate.of(2026, 8, 15)
        );

        // 3. Beyond Blackouts Clinic — EfR real project, DR Congo
        Organization drc = org(
            "Beyond Blackouts Clinic",
            "Solar-powered clinic serving 8,000 displaced people in eastern DR Congo.",
            "Dr. Céleste Nzangi", "+243812345678",
            "Bunia, Ituri Province, DR Congo",
            VerificationStatus.FULLY_VERIFIED,
            List.of("Médecins Sans Frontières", "Energy for Refugees"),
            28_000.0
        );
        project(drc,
            "Clinic Solar Resilience — DRC",
            "Installing a 30 kWp rooftop solar system and 48-hour battery backup " +
            "so the clinic can operate vaccines, surgery lighting, and oxygen through " +
            "grid outages.",
            List.of("solar", "healthcare", "displacement"),
            new BigDecimal("25000"), new BigDecimal("9100"),
            "Ituri Province, DR Congo",
            LocalDate.of(2026, 10, 1)
        );

        // 4. NARA Climate RLO — South Sudan, newly formed, no funding yet
        Organization nara = org(
            "NARA Climate RLO",
            "Refugee-led climate adaptation and clean cooking in South Sudan.",
            "Amara Deng", "+211912345678",
            "Juba, South Sudan",
            VerificationStatus.UNVERIFIED,
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

        // 5. Gaza Energy Recovery — Palestine, vouched
        Organization gaza = org(
            "Gaza Energy Recovery",
            "Emergency solar installations restoring power to clinics and water pumps in Gaza.",
            "Yusuf Al-Masri", "+970591234567",
            "Gaza City, Palestine",
            VerificationStatus.VOUCHED,
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

        // 6. Sahel Sunlight Collective — Niger, early-stage, minimal funding
        Organization sahel = org(
            "Sahel Sunlight Collective",
            "Bringing affordable solar lanterns and phone-charging to off-grid villages in Niger.",
            "Fatima Moussa", "+22796123456",
            "Agadez, Niger",
            VerificationStatus.UNVERIFIED,
            List.of(),
            2_000.0
        );
        project(sahel,
            "Nigerien Village Solar Kits",
            "Bulk procurement and local distribution of 300 solar home systems for " +
            "villages along the Niger–Libya corridor, serving families with no grid access.",
            List.of("solar", "rural", "off-grid"),
            new BigDecimal("8000"), new BigDecimal("500"),
            "Agadez, Niger",
            LocalDate.of(2026, 11, 30)
        );

        // 7. Mekong Clean Fuel Alliance — Laos, mid-funded
        Organization mekong = org(
            "Mekong Clean Fuel Alliance",
            "Replacing open-fire cooking with biomass gasifiers in riverine communities along the Mekong.",
            "Bounsong Phommasack", "+85620123456",
            "Vientiane, Laos",
            VerificationStatus.VOUCHED,
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

        // 8. Rohingya Renewable Network — Bangladesh, highest recent funding (Anti-Matthew demo)
        Organization rohingya = org(
            "Rohingya Renewable Network",
            "Community-owned solar micro-grids inside Cox's Bazar refugee camps, Bangladesh.",
            "Mohammad Alam", "+8801812345678",
            "Cox's Bazar, Bangladesh",
            VerificationStatus.FULLY_VERIFIED,
            List.of("UNHCR", "IRC"),
            55_000.0  // highest recent funding — penalised by Anti-Matthew
        );
        project(rohingya,
            "Cox's Bazar Camp Solar Micro-grid",
            "A 150 kWp community solar plant with pre-paid meters serving 3,000 " +
            "Rohingya households in blocks 11–14, Cox's Bazar.",
            List.of("solar", "refugees", "micro-grid"),
            new BigDecimal("35000"), new BigDecimal("18000"),
            "Cox's Bazar, Bangladesh",
            LocalDate.of(2026, 8, 31)
        );

        // 9. Andean Wind Initiative — Peru, early-stage
        Organization andean = org(
            "Andean Wind Initiative",
            "Small wind turbines powering indigenous highland communities in the Peruvian Andes.",
            "Rosa Quispe", "+51912345678",
            "Puno, Peru",
            VerificationStatus.UNVERIFIED,
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
    }

    // ------------------------------------------------------------------
    // Donors
    // ------------------------------------------------------------------

    private void seedDonors() {

        // 1. Large institutional funder — Africa focus, solar+wind, open to all
        donor("Shell Foundation",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "East Africa", "Kenya"),
            List.of("solar", "wind", "off-grid"),
            new BigDecimal("20000"), new BigDecimal("100000"),
            false
        );

        // 2. UNHCR-style institutional funder — refugee camps, vouched orgs only
        donor("UNHCR Innovation Fund",
            DonorType.INSTITUTIONAL,
            List.of("East Africa", "Kenya", "Bangladesh", "Palestine"),
            List.of("solar", "refugees", "micro-grid"),
            new BigDecimal("10000"), new BigDecimal("50000"),
            true
        );

        // 3. Dutch foundation — broad geography, cookstoves + solar
        donor("EnergieVan Morgen Fonds",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "Asia", "Laos", "Niger"),
            List.of("solar", "cookstoves", "biomass"),
            new BigDecimal("5000"), new BigDecimal("30000"),
            false
        );

        // 4. Individual impact investor — Latin America + Africa, indigenous communities
        donor("Maria Santos",
            DonorType.INDIVIDUAL,
            List.of("Latin America", "Colombia", "Peru", "Africa"),
            List.of("solar", "indigenous", "off-grid"),
            new BigDecimal("2000"), new BigDecimal("15000"),
            false
        );

        // 5. Pan-sector climate tech fund — global, all energy types, large tickets
        donor("ClimateTech Impact Fund",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "Asia", "Latin America", "Middle East"),
            List.of("solar", "wind", "biomass", "cookstoves", "micro-grid"),
            new BigDecimal("15000"), new BigDecimal("80000"),
            false
        );

        // 6. Dutch lottery-funded charity — Africa, healthcare + solar, vouched only
        donor("Postcode Loterij Fonds",
            DonorType.INSTITUTIONAL,
            List.of("Africa", "Congo", "Kenya", "South Sudan"),
            List.of("solar", "healthcare", "displacement"),
            new BigDecimal("10000"), new BigDecimal("40000"),
            true
        );

        // 7. Individual diaspora donor — Gaza / Palestine focus
        donor("Ahmed Al-Masri",
            DonorType.INDIVIDUAL,
            List.of("Palestine", "Gaza", "Middle East"),
            List.of("solar", "emergency", "healthcare"),
            new BigDecimal("1000"), new BigDecimal("8000"),
            false
        );
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Organization org(String name, String mission, String contactName,
            String contactWhatsapp, String hqLocation,
            VerificationStatus status, List<String> vouchedBy,
            double recentFunding) {
        Organization o = new Organization();
        o.setName(name);
        o.setOneSentenceMission(mission);
        o.setContactName(contactName);
        o.setContactWhatsapp(contactWhatsapp);
        o.setHqLocation(hqLocation);
        o.setVerificationStatus(status);
        o.setVouchedBy(vouchedBy);
        o.setRecentFundingReceivedEur(recentFunding);
        return orgRepo.save(o);
    }

    private void project(Organization org, String title, String description,
            List<String> tags, BigDecimal target, BigDecimal current,
            String displayLocation, LocalDate deadline) {
        Project p = new Project();
        p.setOrganization(org);
        p.setTitle(title);
        p.setAiPolishedDescription(description);
        p.setRawInputWhatsapp(description); // raw = polished for seed data
        p.setEnergyFocusTags(tags);
        p.setTargetAmountEur(target);
        p.setCurrentFundingAmountEur(current);
        p.setDisplayLocation(displayLocation);
        p.setFundingDeadline(deadline);
        p.setIsUnlocked(true);
        projectRepo.save(p);
    }

    private void donor(String name, DonorType type, List<String> regions,
            List<String> focus, BigDecimal min, BigDecimal max,
            boolean requiresVouched) {
        Donor d = new Donor();
        d.setName(name);
        d.setDonorType(type);
        d.setPreferredRegions(regions);
        d.setPreferredEnergyFocus(focus);
        d.setMinGivingCapacityEur(min);
        d.setMaxGivingCapacityEur(max);
        d.setRequiresVouchedOnly(requiresVouched);
        donorRepo.save(d);
    }
}
