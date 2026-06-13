package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for an RLO publishing a project from the AI co-pilot. The polished
 * description and original notes come straight from the co-pilot; the RLO adds
 * the concrete fields the matching engine needs (tags, amount, location).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequestDto {

    private String title;
    private String aiPolishedDescription;
    private String rawInputWhatsapp;
    private List<String> energyFocusTags;
    private BigDecimal targetAmountEur;
    private String displayLocation;
    private LocalDate fundingDeadline;
}
