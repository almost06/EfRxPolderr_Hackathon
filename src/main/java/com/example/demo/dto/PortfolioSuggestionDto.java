package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A dynamically suggested basket for a donor who would rather give without
 * picking a single organization. Distinct from the stored {@code Portfolio}
 * entity: this is computed on the fly from the donor's criteria and total gift.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSuggestionDto {

    private BigDecimal totalDonationEur;
    private List<PortfolioAllocationDto> allocations;
    private String summary;
}
