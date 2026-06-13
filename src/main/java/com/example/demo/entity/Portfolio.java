package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private BigDecimal targetAmountEur;
    private BigDecimal currentFundingAmountEur;
    private Boolean isFullyFunded;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTargetAmountEur() {
        return targetAmountEur;
    }

    public void setTargetAmountEur(BigDecimal targetAmountEur) {
        this.targetAmountEur = targetAmountEur;
    }

    public BigDecimal getCurrentFundingAmountEur() {
        return currentFundingAmountEur;
    }

    public void setCurrentFundingAmountEur(BigDecimal currentFundingAmountEur) {
        this.currentFundingAmountEur = currentFundingAmountEur;
    }

    public Boolean getIsFullyFunded() {
        return isFullyFunded;
    }

    public void setIsFullyFunded(Boolean isFullyFunded) {
        this.isFullyFunded = isFullyFunded;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
