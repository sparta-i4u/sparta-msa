package com.i4u.company.presentation.endpoint;

import com.i4u.common.utils.CommonResponse;
import com.i4u.company.application.service.CompanyClientService;
import com.i4u.company.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.company.presentation.dtos.response.OrderCompanyUpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyEndpoint {

    private final CompanyClientService companyClientService;

    @GetMapping("/orders/{supplierCompanyId}/{recipientCompanyId}")
    OrderCompanyResponse confirmCompany(@PathVariable UUID supplierCompanyId, @PathVariable UUID recipientCompanyId) {
        OrderCompanyResponse response = companyClientService.confirmCompany(supplierCompanyId, recipientCompanyId);
        return response;
    }

    @GetMapping("/orders/{companyId}")
    ResponseEntity<CommonResponse<OrderCompanyUpdateResponse>> confirmCompanyUpdate(@PathVariable UUID companyId) {
        OrderCompanyUpdateResponse response = companyClientService.confirmCompanyUpdate(companyId);
        return ResponseEntity.ok(CommonResponse.success(response, "업체 검증 완료"));
    }

    ////상품 만들 때, 진짜 해당하는 업체의 CompanyId가 맞는지
    //    @GetMapping("/api/v1/companies/products/{companyId}")
    //    public Boolean getCompanyIdByProduct(@PathVariable UUID companyId);
    @GetMapping("/products/{companyId}")
    public Boolean confirmCompanyByProduct(@PathVariable UUID companyId) {
        return companyClientService.confirmCompanyByProduct(companyId);
    }
}