package com.example.demo.dto;

import com.example.demo.entity.VerificationStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A project surfaced to a donor. Deliberately carries NO numeric match score:
 * the brief warns that rating systems and performance metrics import market
 * logic into solidarity relationships. We expose a qualitative {@code fitLabel}
 * and a plain-language {@code matchReason} instead — fit, not a leaderboard.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultDto {

    private Long projectId;
    private String projectTitle;
    private Long organizationId;
    private String organizationName;
    private String oneSentenceMission;
    private String displayLocation;
    private List<String> energyFocusTags;
    private BigDecimal targetAmountEur;
    private BigDecimal currentFundingAmountEur;
    private VerificationStatus verificationStatus;
    private List<String> vouchedBy;

    private String fitLabel;
    private String matchReason;
}
