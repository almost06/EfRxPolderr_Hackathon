package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;
import com.example.demo.entity.DonorType;
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
    private String email;
    private DonorType donorType;
    private List<String> preferredRegions;
    private List<String> preferredEnergyFocus;
    private BigDecimal minGivingCapacityEur;
    private BigDecimal maxGivingCapacityEur;
    private BigDecimal totalDonatedEur;
    private Boolean requiresVouchedOnly;
    private List<String> volunteerSkills;
}
