package com.example.demo.mapper;

import com.example.demo.dto.PortfolioDto;
import com.example.demo.dto.ProjectDto;
import com.example.demo.entity.Portfolio;
import com.example.demo.entity.Project;
import java.util.List;

public final class PortfolioMapper {

    private PortfolioMapper() {
    }

    public static PortfolioDto toDto(Portfolio entity) {
        if (entity == null) {
            return null;
        }

        return PortfolioDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .targetAmountEur(entity.getTargetAmountEur())
                .currentFundingAmountEur(entity.getCurrentFundingAmountEur())
                .isFullyFunded(entity.getIsFullyFunded())
                .projects(toProjectDtos(entity.getProjects()))
                .build();
    }

    public static Portfolio toEntity(PortfolioDto dto) {
        if (dto == null) {
            return null;
        }

        Portfolio entity = new Portfolio();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setTargetAmountEur(dto.getTargetAmountEur());
        entity.setCurrentFundingAmountEur(dto.getCurrentFundingAmountEur());
        entity.setIsFullyFunded(dto.getIsFullyFunded());
        entity.setProjects(toProjects(dto.getProjects(), entity));
        return entity;
    }

    private static List<ProjectDto> toProjectDtos(List<Project> projects) {
        if (projects == null) {
            return null;
        }

        return projects.stream()
                .map(ProjectMapper::toDto)
                .toList();
    }

    private static List<Project> toProjects(List<ProjectDto> projectDtos, Portfolio portfolio) {
        if (projectDtos == null) {
            return null;
        }

        return projectDtos.stream()
                .map(projectDto -> ProjectMapper.toEntity(projectDto, portfolio))
                .toList();
    }
}
