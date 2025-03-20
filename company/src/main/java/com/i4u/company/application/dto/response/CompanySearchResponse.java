package com.i4u.company.application.dto.response;

import com.i4u.company.domain.entity.Company;
import org.springframework.data.domain.Page;

import java.util.List;

public record CompanySearchResponse(List<CompanyResponse> companies, int totalPages,
                                    long totalElements) {

    public static CompanySearchResponse of(Page<Company> companyPages) {
        return new CompanySearchResponse(companyPages.map(CompanyResponse::of).toList(),
                companyPages.getTotalPages(), companyPages.getTotalElements());
    }
}