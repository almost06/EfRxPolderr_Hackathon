package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {

    private Long id;
    private String title;
    private String description;
    private BigDecimal targetAmountEur;
    private BigDecimal currentFundingAmountEur;
    private Boolean isFullyFunded;
    private List<ProjectDto> projects;
}
