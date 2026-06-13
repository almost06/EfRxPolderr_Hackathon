package com.example.demo.controller;

import com.example.demo.dto.DonorDto;
import com.example.demo.mapper.DonorMapper;
import com.example.demo.repository.DonorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/donors")
public class DonorAccountController {

    private final DonorRepository donorRepository;

    public DonorAccountController(DonorRepository donorRepository) {
        this.donorRepository = donorRepository;
    }

    @GetMapping("/{donorId}")
    public ResponseEntity<DonorDto> getDonor(@PathVariable Long donorId) {
        return donorRepository.findById(donorId)
                .map(DonorMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
