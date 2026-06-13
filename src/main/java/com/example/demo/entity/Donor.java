package com.example.demo.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private DonorType donorType;

    @ElementCollection
    @CollectionTable(name = "donor_preferred_regions", joinColumns = @JoinColumn(name = "donor_id"))
    @Column(name = "region")
    private List<String> preferredRegions = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "donor_preferred_energy_focus", joinColumns = @JoinColumn(name = "donor_id"))
    @Column(name = "energy_focus")
    private List<String> preferredEnergyFocus = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "donor_volunteer_skills", joinColumns = @JoinColumn(name = "donor_id"))
    @Column(name = "volunteer_skills")
    private List<String> volunteerSkills = new ArrayList<>();

    private BigDecimal minGivingCapacityEur;
    private BigDecimal maxGivingCapacityEur;
    private BigDecimal totalDonatedEur;
    private Boolean requiresVouchedOnly;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DonorType getDonorType() {
        return donorType;
    }

    public void setDonorType(DonorType donorType) {
        this.donorType = donorType;
    }

    public List<String> getPreferredRegions() {
        return preferredRegions;
    }

    public void setPreferredRegions(List<String> preferredRegions) {
        this.preferredRegions = preferredRegions;
    }

    public List<String> getPreferredEnergyFocus() {
        return preferredEnergyFocus;
    }

    public void setPreferredEnergyFocus(List<String> preferredEnergyFocus) {
        this.preferredEnergyFocus = preferredEnergyFocus;
    }

    public List<String> getVolunteerSkills() {
        return volunteerSkills;
    }

    public void setVolunteerSkills(List<String> volunteerSkills) {
        this.volunteerSkills = volunteerSkills;
    }

    public BigDecimal getMinGivingCapacityEur() {
        return minGivingCapacityEur;
    }

    public void setMinGivingCapacityEur(BigDecimal minGivingCapacityEur) {
        this.minGivingCapacityEur = minGivingCapacityEur;
    }

    public BigDecimal getMaxGivingCapacityEur() {
        return maxGivingCapacityEur;
    }

    public void setMaxGivingCapacityEur(BigDecimal maxGivingCapacityEur) {
        this.maxGivingCapacityEur = maxGivingCapacityEur;
    }

    public BigDecimal getTotalDonatedEur() {
        return totalDonatedEur;
    }

    public void setTotalDonatedEur(BigDecimal totalDonatedEur) {
        this.totalDonatedEur = totalDonatedEur;
    }

    public Boolean getRequiresVouchedOnly() {
        return requiresVouchedOnly;
    }

    public void setRequiresVouchedOnly(Boolean requiresVouchedOnly) {
        this.requiresVouchedOnly = requiresVouchedOnly;
    }
}
