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
import java.util.ArrayList;
import java.util.List;

@Entity
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private OrganizationType organizationType;

    @Column(length = 150)
    private String oneSentenceMission;

    @Column(length = 2000)
    private String description;

    private String contactName;
    private String contactEmail;
    private String contactWhatsapp;
    private String onlinePresenceUrl;
    private String hqLocation;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @ElementCollection
    @CollectionTable(name = "organization_vouched_by", joinColumns = @JoinColumn(name = "organization_id"))
    @Column(name = "voucher")
    private List<String> vouchedBy = new ArrayList<>();

    private Double recentFundingReceivedEur;
    private Double matchScoreAdjustment;

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

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public String getOneSentenceMission() {
        return oneSentenceMission;
    }

    public void setOneSentenceMission(String oneSentenceMission) {
        this.oneSentenceMission = oneSentenceMission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactWhatsapp() {
        return contactWhatsapp;
    }

    public void setContactWhatsapp(String contactWhatsapp) {
        this.contactWhatsapp = contactWhatsapp;
    }

    public String getOnlinePresenceUrl() {
        return onlinePresenceUrl;
    }

    public void setOnlinePresenceUrl(String onlinePresenceUrl) {
        this.onlinePresenceUrl = onlinePresenceUrl;
    }

    public String getHqLocation() {
        return hqLocation;
    }

    public void setHqLocation(String hqLocation) {
        this.hqLocation = hqLocation;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public List<String> getVouchedBy() {
        return vouchedBy;
    }

    public void setVouchedBy(List<String> vouchedBy) {
        this.vouchedBy = vouchedBy;
    }

    public Double getRecentFundingReceivedEur() {
        return recentFundingReceivedEur;
    }

    public void setRecentFundingReceivedEur(Double recentFundingReceivedEur) {
        this.recentFundingReceivedEur = recentFundingReceivedEur;
    }

    public Double getMatchScoreAdjustment() {
        return matchScoreAdjustment;
    }

    public void setMatchScoreAdjustment(Double matchScoreAdjustment) {
        this.matchScoreAdjustment = matchScoreAdjustment;
    }
}
