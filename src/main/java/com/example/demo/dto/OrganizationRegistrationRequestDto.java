package com.example.demo.dto;

import com.example.demo.entity.OrganizationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationRegistrationRequestDto {

    private String name;
    private OrganizationType organizationType;
    private String description;
    private String contactEmail;
    private String password;
}
