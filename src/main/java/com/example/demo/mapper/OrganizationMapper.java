package com.example.demo.mapper;

import com.example.demo.dto.OrganizationDto;
import com.example.demo.entity.Organization;
import java.util.ArrayList;
import java.util.List;

public final class OrganizationMapper {

    private OrganizationMapper() {
    }

    public static OrganizationDto toDto(Organization entity) {
        if (entity == null) {
            return null;
        }

        return OrganizationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .organizationType(entity.getOrganizationType())
                .oneSentenceMission(entity.getOneSentenceMission())
                .description(entity.getDescription())
                .contactName(entity.getContactName())
                .contactEmail(entity.getContactEmail())
                .contactWhatsapp(entity.getContactWhatsapp())
                .onlinePresenceUrl(entity.getOnlinePresenceUrl())
                .hqLocation(entity.getHqLocation())
                .verificationStatus(entity.getVerificationStatus())
                .vouchedBy(copyList(entity.getVouchedBy()))
                .recentFundingReceivedEur(entity.getRecentFundingReceivedEur())
                .matchScoreAdjustment(entity.getMatchScoreAdjustment())
                .totalDonatedEur(entity.getTotalDonatedEur())
                .build();
    }

    public static Organization toEntity(OrganizationDto dto) {
        if (dto == null) {
            return null;
        }

        Organization entity = new Organization();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setOrganizationType(dto.getOrganizationType());
        entity.setOneSentenceMission(dto.getOneSentenceMission());
        entity.setDescription(dto.getDescription());
        entity.setContactName(dto.getContactName());
        entity.setContactEmail(dto.getContactEmail());
        entity.setContactWhatsapp(dto.getContactWhatsapp());
        entity.setOnlinePresenceUrl(dto.getOnlinePresenceUrl());
        entity.setHqLocation(dto.getHqLocation());
        entity.setVerificationStatus(dto.getVerificationStatus());
        entity.setVouchedBy(copyList(dto.getVouchedBy()));
        entity.setRecentFundingReceivedEur(dto.getRecentFundingReceivedEur());
        entity.setMatchScoreAdjustment(dto.getMatchScoreAdjustment());
        entity.setTotalDonatedEur(dto.getTotalDonatedEur());
        return entity;
    }

    private static List<String> copyList(List<String> values) {
        return values == null ? null : new ArrayList<>(values);
    }
}
