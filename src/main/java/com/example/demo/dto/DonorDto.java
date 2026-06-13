package com.example.demo.dto;

import com.example.demo.entity.DonorType;
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
public class DonorDto {

    private Long id;
    private String name;
    private DonorType donorType;
    private List<String> preferredRegions;
    private List<String> preferredEnergyFocus;
    private BigDecimal minGivingCapacityEur;
    private BigDecimal maxGivingCapacityEur;
    private Boolean requiresVouchedOnly;
}
