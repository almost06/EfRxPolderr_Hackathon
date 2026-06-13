package com.example.demo.dto;

import com.example.demo.entity.VerificationStatus;
import com.example.demo.entity.OrganizationType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto {

    private Long id;
    private String name;
    private OrganizationType organizationType;
    private String oneSentenceMission;
    private String description;
    private String contactName;
    private String contactEmail;
    private String contactWhatsapp;
    private String onlinePresenceUrl;
    private String hqLocation;
    private VerificationStatus verificationStatus;
    private List<String> vouchedBy;
    private Double recentFundingReceivedEur;
    private Double matchScoreAdjustment;
}
