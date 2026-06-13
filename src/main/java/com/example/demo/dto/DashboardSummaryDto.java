package com.example.demo.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    private Long id;
    private String name;
    private String role;
    private String email;
    private BigDecimal totalDonatedEur;
    private BigDecimal totalReceivedEur;
}
