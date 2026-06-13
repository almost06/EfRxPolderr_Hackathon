package com.example.demo.dto;

import com.example.demo.entity.DonorType;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A donor surfaced to an RLO. Same principle as {@link MatchResultDto}: a
 * qualitative fit label and reason, never a numeric ranking. Gives the RLO
 * enough to decide who to approach without exposing donors as a scored list.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonorMatchResultDto {

    private Long donorId;
    private String donorName;
    private DonorType donorType;
    private List<String> preferredRegions;
    private List<String> preferredEnergyFocus;
    private BigDecimal minGivingCapacityEur;
    private BigDecimal maxGivingCapacityEur;

    private String fitLabel;
    private String matchReason;
}
