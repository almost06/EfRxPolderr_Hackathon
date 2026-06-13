package com.example.demo.controller;

import com.example.demo.dto.CreateProjectRequestDto;
import com.example.demo.dto.OrganizationDto;
import com.example.demo.dto.ProjectDto;
import com.example.demo.entity.Organization;
import com.example.demo.entity.Project;
import com.example.demo.mapper.OrganizationMapper;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.repository.OrganizationRepository;
import com.example.demo.repository.ProjectRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * Publish a project for an organization. Called by the AI co-pilot's
     * "Publish as project" action — the polished profile plus the concrete
     * fields (tags, amount, location) become a live, matchable project that
     * donors can immediately discover. This closes the loop from rough notes
     * to fundable project.
     */
    @PostMapping("/{organizationId}/projects")
    public ResponseEntity<ProjectDto> createProject(
            @PathVariable Long organizationId,
            @RequestBody CreateProjectRequestDto request) {

        Organization org = organizationRepository.findById(organizationId).orElse(null);
        if (org == null) {
            return ResponseEntity.notFound().build();
        }
        if (request == null || request.getTitle() == null || request.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Project project = new Project();
        project.setOrganization(org);
        project.setTitle(request.getTitle().trim());
        project.setAiPolishedDescription(request.getAiPolishedDescription());
        project.setRawInputWhatsapp(request.getRawInputWhatsapp());
        project.setEnergyFocusTags(request.getEnergyFocusTags() == null
                ? new ArrayList<>() : request.getEnergyFocusTags());
        project.setTargetAmountEur(request.getTargetAmountEur());
        project.setCurrentFundingAmountEur(BigDecimal.ZERO);
        project.setDisplayLocation(request.getDisplayLocation());
        project.setFundingDeadline(request.getFundingDeadline());
        project.setIsUnlocked(true);

        Project saved = projectRepository.save(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectMapper.toDto(saved));
    }
}
