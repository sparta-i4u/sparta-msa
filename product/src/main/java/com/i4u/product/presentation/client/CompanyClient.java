package com.i4u.product.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
//Company로 요청을 쏴주는 친구 Client
public interface CompanyClient {

    // 받아주는 쪽 에서는 hubId가 있으면 UUID로 주고, 없으면 null을 반환할 예정
    @GetMapping("/api/v1/companies/products/{userId}")
    public UUID getCompanyId(@PathVariable UUID userId);

}
