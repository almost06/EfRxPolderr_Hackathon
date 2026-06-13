package com.example.demo.dto;

import com.example.demo.entity.DonorType;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonorRegistrationRequestDto {

    private String name;
    private String email;
    private String password;
    private DonorType donorType;
    private List<String> preferredRegions;
    private List<String> preferredEnergyFocus;
    private List<String> volunteerSkills;
    private BigDecimal minGivingCapacityEur;
    private BigDecimal maxGivingCapacityEur;
    private Boolean requiresVouchedOnly;
}
