package com.example.demo.repository;

import com.example.demo.entity.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByPortfolioId(Long portfolioId);

    List<Project> findByOrganizationId(Long organizationId);
}
