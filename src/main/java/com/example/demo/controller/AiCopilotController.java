package com.example.demo.controller;

import com.example.demo.dto.OnboardRequestDto;
import com.example.demo.dto.OnboardResponseDto;
import com.example.demo.service.AiCopilotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI Grant-Writing Co-Pilot endpoint.
 *
 *  POST /api/onboard   rough field notes -> polished donor-ready profile
 *
 * CORS is open so the browser frontend can call it directly during the hackathon.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AiCopilotController {

    private final AiCopilotService aiCopilotService;

    public AiCopilotController(AiCopilotService aiCopilotService) {
        this.aiCopilotService = aiCopilotService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<OnboardResponseDto> onboard(@RequestBody OnboardRequestDto request) {
        if (request == null || request.getRawNotes() == null || request.getRawNotes().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(aiCopilotService.polish(request));
    }
}
