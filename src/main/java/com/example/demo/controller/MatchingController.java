package com.example.demo.controller;

import com.example.demo.dto.DonorMatchRequestDto;
import com.example.demo.dto.DonorMatchResultDto;
import com.example.demo.dto.MatchResultDto;
import com.example.demo.dto.PortfolioSuggestionDto;
import com.example.demo.dto.ProjectMatchRequestDto;
import com.example.demo.service.MatchingService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bidirectional discovery endpoints.
 *
 *  POST /api/match/projects   donor criteria  -> matching projects
 *  POST /api/match/donors     project details -> matching donors
 *  POST /api/match/portfolio  donor criteria + total gift -> suggested basket
 *
 * CORS is open so the Lovable / browser frontend can call it directly during
 * the hackathon.
 */
@RestController
@RequestMapping("/api/match")
@CrossOrigin(origins = "*")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    /** Donor side of blind search: declare criteria, receive projects. */
    @PostMapping("/projects")
    public List<MatchResultDto> matchProjects(@RequestBody DonorMatchRequestDto request) {
        return matchingService.findProjectsForDonor(request);
    }

    /** RLO side: describe a project, receive aligned donors to approach. */
    @PostMapping("/donors")
    public List<DonorMatchResultDto> matchDonors(@RequestBody ProjectMatchRequestDto request) {
        return matchingService.findDonorsForProject(request);
    }

    /** Give without choosing one organization: a gap-weighted suggested basket. */
    @PostMapping("/portfolio")
    public PortfolioSuggestionDto suggestPortfolio(
            @RequestBody DonorMatchRequestDto request,
            @RequestParam BigDecimal totalDonation) {
        return matchingService.suggestPortfolio(request, totalDonation);
    }
}
