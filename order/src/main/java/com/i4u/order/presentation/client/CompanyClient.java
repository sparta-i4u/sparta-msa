package com.i4u.order.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.i4u.order.presentation.dtos.request.OrderCompanyRequest;
import com.i4u.order.presentation.dtos.request.OrderCompanyUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.order.presentation.dtos.response.OrderCompanyUpdateResponse;

@FeignClient(name = "company")
public interface CompanyClient {

	@GetMapping("/companies/{companyId}")
	OrderCompanyResponse confirmCompany(@ModelAttribute OrderCompanyRequest request /*userId, userRole or JWT 필요*/);

	@GetMapping("/companies/{companyId}/update-confirm")
	OrderCompanyUpdateResponse confirmCompanyUpdate(@ModelAttribute OrderCompanyUpdateRequest request /*userId, userRole or JWT 필요*/);

}