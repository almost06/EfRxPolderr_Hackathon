package com.example.demo.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One line of a suggested portfolio: how much of the donor's total gift goes to
 * a given project, and why. The split is weighted by remaining funding gap so
 * the marginal euro lands where it closes the most distance.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAllocationDto {

    private Long projectId;
    private String projectTitle;
    private String organizationName;
    private String displayLocation;
    private BigDecimal suggestedAmountEur;
    private String reason;
}
