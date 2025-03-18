package com.i4u.company.application.dto.response;

import com.i4u.company.domain.Company;

import java.util.UUID;

public record CompanyResponseDto(
        UUID companyId,
        UUID hubId,
        String name,
        String type,
        String owner,
        String address,
        String number

) {

    public static CompanyResponseDto of(Company company) {
        return new CompanyResponseDto(
                company.getId(),
                company.getHubId(),
                company.getName(),
                company.getType(),
                company.getOwner(),
                company.getAddress(),
                company.getNumber()
        );
    }
}