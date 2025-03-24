package com.i4u.order.presentation.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.presentation.dtos.request.OrderCompanyRequest;
import com.i4u.order.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.order.presentation.dtos.response.OrderCompanyUpdateResponse;

@FeignClient(name = "COMPANY-SERVICE")
public interface CompanyClient {

	@GetMapping("/api/v1/companies/orders/{supplierCompanyId}/{recipientCompanyId}")
	OrderCompanyResponse confirmCompany(@PathVariable UUID supplierCompanyId, @PathVariable UUID recipientCompanyId);

	@GetMapping("/api/v1/companies/{companyId}/orders")
	ResponseEntity<CommonResponse<OrderCompanyUpdateResponse>> confirmCompanyUpdate(@PathVariable UUID companyId /*userId, userRole or JWT 필요*/);

	@GetMapping("/api/v1/companies/{companyId}")
	UUID getCompanyId(@PathVariable UUID companyId);
}