package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What an RLO submits to discover donors. The RLO side of matching: an
 * organization describes its project and finds donors whose preferences align,
 * so it can reach out instead of waiting to be found.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMatchRequestDto {

    private String location;
    private List<String> energyFocusTags;
    private BigDecimal targetAmountEur;
    private Boolean isVouched;
}
