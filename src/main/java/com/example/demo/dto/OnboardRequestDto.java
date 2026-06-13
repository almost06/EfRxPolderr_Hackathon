package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rough field notes from an RLO, sent to the AI grant-writing co-pilot.
 * {@code language} is optional; defaults to English when blank.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnboardRequestDto {

    private String rawNotes;
    private String language;
}
