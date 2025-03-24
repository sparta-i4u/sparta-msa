package com.i4u.company.application.service;

import com.i4u.company.domain.entity.Company;
import com.i4u.company.domain.repository.CompanyRepository;
import com.i4u.company.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.company.presentation.dtos.response.OrderCompanyUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyClientService {

    private final CompanyRepository companyRepository;

    public OrderCompanyResponse confirmCompany(UUID supplierCompanyId, UUID recipientCompanyId) {
        Company supplierCompany = companyRepository.findById(supplierCompanyId)
                .orElseThrow(() -> new IllegalArgumentException("공급 업체가 없습니다."));
        Company recipientCompany = companyRepository.findById(recipientCompanyId)
                .orElseThrow(() -> new IllegalArgumentException("수령 업체가 없습니다."));

        OrderCompanyResponse response = OrderCompanyResponse.builder()
                .supplierId(supplierCompany.getId())
                .supplierHubId(supplierCompany.getHubId())
                .recipientId(recipientCompany.getId())
                .recipientHubId(recipientCompany.getHubId())
                .address(recipientCompany.getAddress())
                .isDeleted(false)
                .build();

        return response;
    }

    public OrderCompanyUpdateResponse confirmCompanyUpdate(UUID companyId) {
        Company supplierCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("수령 업체가 없습니다."));

        OrderCompanyUpdateResponse response = OrderCompanyUpdateResponse.builder()
                .supplierId(supplierCompany.getId())
                .supplierHubId(supplierCompany.getHubId())
                .isDeleted(false)
                .build();

        return response;
    }

    //product에서 만든 상품의 CompanyId를 검증하는 메소드
    public Boolean confirmCompanyByProduct(UUID companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if(company == null){
            return false;
        }return true;
    }
}