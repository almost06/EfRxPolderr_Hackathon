package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What a donor submits to discover projects. Anonymous: no login required,
 * the donor declares their criteria up front (blind search — names are revealed
 * only in the results, never browsed by reputation first).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonorMatchRequestDto {

    private List<String> preferredRegions;
    private List<String> preferredEnergyFocus;
    private BigDecimal minGivingCapacityEur;
    private BigDecimal maxGivingCapacityEur;
    private Boolean requiresVouchedOnly;
}
