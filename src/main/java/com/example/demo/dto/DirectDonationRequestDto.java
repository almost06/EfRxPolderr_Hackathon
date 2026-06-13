package com.example.demo.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectDonationRequestDto {

    private Long accountId;
    private String role;
    private Long projectId;
    private BigDecimal amountEur;
}
