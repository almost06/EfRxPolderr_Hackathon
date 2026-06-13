package com.example.demo.mapper;

import com.example.demo.dto.DonorDto;
import com.example.demo.entity.Donor;
import java.util.ArrayList;
import java.util.List;

public final class DonorMapper {

    private DonorMapper() {
    }

    public static DonorDto toDto(Donor entity) {
        if (entity == null) {
            return null;
        }

        return DonorDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .donorType(entity.getDonorType())
                .preferredRegions(copyList(entity.getPreferredRegions()))
                .preferredEnergyFocus(copyList(entity.getPreferredEnergyFocus()))
                .volunteerSkills(copyList(entity.getVolunteerSkills()))
                .minGivingCapacityEur(entity.getMinGivingCapacityEur())
                .maxGivingCapacityEur(entity.getMaxGivingCapacityEur())
                .requiresVouchedOnly(entity.getRequiresVouchedOnly())
                .build();
    }

    public static Donor toEntity(DonorDto dto) {
        if (dto == null) {
            return null;
        }

        Donor entity = new Donor();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setDonorType(dto.getDonorType());
        entity.setPreferredRegions(copyList(dto.getPreferredRegions()));
        entity.setPreferredEnergyFocus(copyList(dto.getPreferredEnergyFocus()));
        entity.setVolunteerSkills(copyList(dto.getVolunteerSkills()));
        entity.setMinGivingCapacityEur(dto.getMinGivingCapacityEur());
        entity.setMaxGivingCapacityEur(dto.getMaxGivingCapacityEur());
        entity.setRequiresVouchedOnly(dto.getRequiresVouchedOnly());
        return entity;
    }

    private static List<String> copyList(List<String> values) {
        return values == null ? null : new ArrayList<>(values);
    }
}
