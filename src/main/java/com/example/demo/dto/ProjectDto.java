package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private Long id;
    private Long organizationId;
    private String title;
    private String rawInputWhatsapp;
    private String aiPolishedDescription;
    private List<String> energyFocusTags;
    private List<String> skillsNeededTags;
    private BigDecimal targetAmountEur;
    private BigDecimal currentFundingAmountEur;
    private LocalDate fundingDeadline;
    private Boolean isUnlocked;
    private Double privateLatitude;
    private Double privateLongitude;
    private String displayLocation;
    private String verifiedImageUrl;
    private Long portfolioId;
}
