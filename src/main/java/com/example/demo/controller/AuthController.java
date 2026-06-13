package com.example.demo.controller;

import com.example.demo.dto.DonorRegistrationRequestDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.OrganizationRegistrationRequestDto;
import com.example.demo.entity.Donor;
import com.example.demo.entity.DonorType;
import com.example.demo.entity.Organization;
import com.example.demo.entity.OrganizationType;
import com.example.demo.entity.VerificationStatus;
import com.example.demo.repository.DonorRepository;
import com.example.demo.repository.OrganizationRepository;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OrganizationRepository organizationRepository;
    private final DonorRepository donorRepository;

    public AuthController(OrganizationRepository organizationRepository, DonorRepository donorRepository) {
        this.organizationRepository = organizationRepository;
        this.donorRepository = donorRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = request.getEmail().trim();

        return organizationRepository.findByContactEmailIgnoreCase(email)
                .map(this::toOrganizationLoginResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> donorRepository.findByEmailIgnoreCase(email)
                        .map(this::toDonorLoginResponse)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @PostMapping("/register/donor")
    public ResponseEntity<LoginResponseDto> registerDonor(@RequestBody DonorRegistrationRequestDto request) {
        if (request == null || isBlank(request.getName()) || isBlank(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        String email = request.getEmail().trim();
        if (emailExists(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Donor donor = new Donor();
        donor.setName(request.getName().trim());
        donor.setEmail(email);
        donor.setDonorType(request.getDonorType() == null ? DonorType.INDIVIDUAL : request.getDonorType());
        donor.setTotalDonatedEur(BigDecimal.ZERO);
        donor.setRequiresVouchedOnly(false);

        Donor savedDonor = donorRepository.save(donor);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDonorLoginResponse(savedDonor));
    }

    @PostMapping("/register/organization")
    public ResponseEntity<LoginResponseDto> registerOrganization(
            @RequestBody OrganizationRegistrationRequestDto request) {
        if (request == null || isBlank(request.getName()) || isBlank(request.getContactEmail())) {
            return ResponseEntity.badRequest().build();
        }

        String email = request.getContactEmail().trim();
        if (emailExists(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Organization organization = new Organization();
        organization.setName(request.getName().trim());
        organization.setOrganizationType(
                request.getOrganizationType() == null ? OrganizationType.NGO : request.getOrganizationType());
        organization.setDescription(request.getDescription());
        organization.setContactName(request.getName().trim());
        organization.setContactEmail(email);
        organization.setVerificationStatus(VerificationStatus.UNVERIFIED);
        organization.setRecentFundingReceivedEur(0.0);
        organization.setMatchScoreAdjustment(0.0);
        organization.setTotalDonatedEur(BigDecimal.ZERO);

        Organization savedOrganization = organizationRepository.save(organization);
        return ResponseEntity.status(HttpStatus.CREATED).body(toOrganizationLoginResponse(savedOrganization));
    }

    private LoginResponseDto toOrganizationLoginResponse(Organization organization) {
        return LoginResponseDto.builder()
                .id(organization.getId())
                .name(organization.getName())
                .role("ORGANIZATION")
                .email(organization.getContactEmail())
                .build();
    }

    private LoginResponseDto toDonorLoginResponse(Donor donor) {
        return LoginResponseDto.builder()
                .id(donor.getId())
                .name(donor.getName())
                .role("DONOR")
                .email(donor.getEmail())
                .build();
    }

    private boolean emailExists(String email) {
        return organizationRepository.existsByContactEmailIgnoreCase(email)
                || donorRepository.existsByEmailIgnoreCase(email);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
