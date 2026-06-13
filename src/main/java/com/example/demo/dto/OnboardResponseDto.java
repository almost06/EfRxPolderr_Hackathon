package com.example.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A polished, donor-ready project profile produced by the AI co-pilot.
 * Field names match the frontend {@code OnboardResponse} contract exactly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardResponseDto {

    private String profileName;
    private String problemStatement;
    private String beneficiaries;
    private String budget;
    private List<String> sdgTags;
}
