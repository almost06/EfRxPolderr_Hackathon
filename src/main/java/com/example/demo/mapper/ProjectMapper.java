package com.example.demo.mapper;

import com.example.demo.dto.ProjectDto;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Portfolio;
import com.example.demo.entity.Project;
import java.util.ArrayList;
import java.util.List;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static ProjectDto toDto(Project entity) {
        if (entity == null) {
            return null;
        }

        return ProjectDto.builder()
                .id(entity.getId())
                .organizationId(getOrganizationId(entity))
                .title(entity.getTitle())
                .rawInputWhatsapp(entity.getRawInputWhatsapp())
                .aiPolishedDescription(entity.getAiPolishedDescription())
                .energyFocusTags(copyList(entity.getEnergyFocusTags()))
                .skillsNeededTags(copyList(entity.getSkillsNeededTags()))
                .targetAmountEur(entity.getTargetAmountEur())
                .currentFundingAmountEur(entity.getCurrentFundingAmountEur())
                .fundingDeadline(entity.getFundingDeadline())
                .isUnlocked(entity.getIsUnlocked())
                .privateLatitude(entity.getPrivateLatitude())
                .privateLongitude(entity.getPrivateLongitude())
                .displayLocation(entity.getDisplayLocation())
                .verifiedImageUrl(entity.getVerifiedImageUrl())
                .portfolioId(getPortfolioId(entity))
                .build();
    }

    public static Project toEntity(ProjectDto dto) {
        if (dto == null) {
            return null;
        }

        Project entity = new Project();
        entity.setId(dto.getId());
        entity.setOrganization(toOrganizationReference(dto.getOrganizationId()));
        entity.setTitle(dto.getTitle());
        entity.setRawInputWhatsapp(dto.getRawInputWhatsapp());
        entity.setAiPolishedDescription(dto.getAiPolishedDescription());
        entity.setEnergyFocusTags(copyList(dto.getEnergyFocusTags()));
        entity.setSkillsNeededTags(copyList(dto.getSkillsNeededTags()));
        entity.setTargetAmountEur(dto.getTargetAmountEur());
        entity.setCurrentFundingAmountEur(dto.getCurrentFundingAmountEur());
        entity.setFundingDeadline(dto.getFundingDeadline());
        entity.setIsUnlocked(dto.getIsUnlocked());
        entity.setPrivateLatitude(dto.getPrivateLatitude());
        entity.setPrivateLongitude(dto.getPrivateLongitude());
        entity.setDisplayLocation(dto.getDisplayLocation());
        entity.setVerifiedImageUrl(dto.getVerifiedImageUrl());
        entity.setPortfolio(toPortfolioReference(dto.getPortfolioId()));
        return entity;
    }

    static Project toEntity(ProjectDto dto, Portfolio portfolio) {
        Project project = toEntity(dto);
        if (project != null) {
            project.setPortfolio(portfolio);
        }
        return project;
    }

    private static Long getOrganizationId(Project entity) {
        return entity.getOrganization() == null ? null : entity.getOrganization().getId();
    }

    private static Long getPortfolioId(Project entity) {
        return entity.getPortfolio() == null ? null : entity.getPortfolio().getId();
    }

    private static Organization toOrganizationReference(Long organizationId) {
        if (organizationId == null) {
            return null;
        }

        Organization organization = new Organization();
        organization.setId(organizationId);
        return organization;
    }

    private static Portfolio toPortfolioReference(Long portfolioId) {
        if (portfolioId == null) {
            return null;
        }

        Portfolio portfolio = new Portfolio();
        portfolio.setId(portfolioId);
        return portfolio;
    }

    private static List<String> copyList(List<String> values) {
        return values == null ? null : new ArrayList<>(values);
    }
}
