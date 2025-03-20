package com.i4u.company.application.dto.response;

import com.i4u.company.domain.entity.Company;
import com.i4u.company.domain.entity.CompanyType;

import java.util.UUID;

public record CompanyResponse(
        UUID companyId,
        UUID hubId,
        String name,
        CompanyType type,
        UUID owner,
        String address,
        String number
) {
    public static CompanyResponse of(Company company) {
        return new CompanyResponse(
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