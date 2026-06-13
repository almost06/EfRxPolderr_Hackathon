package com.example.demo.controller;

import com.example.demo.dto.OrganizationDto;
import com.example.demo.dto.ProjectDto;
import com.example.demo.mapper.OrganizationMapper;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.ProjectRepository;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/organizations")
public class OrganizationProjectController {

    private final OrganizationRepository organizationRepository;
    private final ProjectRepository projectRepository;

    public OrganizationProjectController(OrganizationRepository organizationRepository, ProjectRepository projectRepository) {
        this.organizationRepository = organizationRepository;
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<OrganizationDto> getOrganizations() {
        return organizationRepository.findAll().stream()
                .map(OrganizationMapper::toDto)
                .toList();
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<OrganizationDto> getOrganization(@PathVariable Long organizationId) {
        return organizationRepository.findById(organizationId)
                .map(OrganizationMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{organizationId}/projects")
    public List<ProjectDto> getOrganizationProjects(@PathVariable Long organizationId) {
        return projectRepository.findByOrganizationId(organizationId).stream()
                .map(ProjectMapper::toDto)
                .toList();
    }
}
