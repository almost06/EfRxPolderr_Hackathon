package com.example.demo.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private String title;

    @Column(length = 2000)
    private String rawInputWhatsapp;

    @Column(length = 4000)
    private String aiPolishedDescription;

    @ElementCollection
    @CollectionTable(name = "project_energy_focus_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    private List<String> energyFocusTags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "project_skills_needed_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    private List<String> skillsNeededTags = new ArrayList<>();

    private BigDecimal targetAmountEur;
    private BigDecimal currentFundingAmountEur;
    private LocalDate fundingDeadline;
    private Boolean isUnlocked;
    private Double privateLatitude;
    private Double privateLongitude;
    private String displayLocation;
    private String verifiedImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRawInputWhatsapp() {
        return rawInputWhatsapp;
    }

    public void setRawInputWhatsapp(String rawInputWhatsapp) {
        this.rawInputWhatsapp = rawInputWhatsapp;
    }

    public String getAiPolishedDescription() {
        return aiPolishedDescription;
    }

    public void setAiPolishedDescription(String aiPolishedDescription) {
        this.aiPolishedDescription = aiPolishedDescription;
    }

    public List<String> getEnergyFocusTags() {
        return energyFocusTags;
    }

    public void setEnergyFocusTags(List<String> energyFocusTags) {
        this.energyFocusTags = energyFocusTags;
    }

    public List<String> getSkillsNeededTags() {
        return skillsNeededTags;
    }

    public void setSkillsNeededTags(List<String> skillsNeededTags) {
        this.skillsNeededTags = skillsNeededTags;
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

    public LocalDate getFundingDeadline() {
        return fundingDeadline;
    }

    public void setFundingDeadline(LocalDate fundingDeadline) {
        this.fundingDeadline = fundingDeadline;
    }

    public Boolean getIsUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(Boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public Double getPrivateLatitude() {
        return privateLatitude;
    }

    public void setPrivateLatitude(Double privateLatitude) {
        this.privateLatitude = privateLatitude;
    }

    public Double getPrivateLongitude() {
        return privateLongitude;
    }

    public void setPrivateLongitude(Double privateLongitude) {
        this.privateLongitude = privateLongitude;
    }

    public String getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(String displayLocation) {
        this.displayLocation = displayLocation;
    }

    public String getVerifiedImageUrl() {
        return verifiedImageUrl;
    }

    public void setVerifiedImageUrl(String verifiedImageUrl) {
        this.verifiedImageUrl = verifiedImageUrl;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}
