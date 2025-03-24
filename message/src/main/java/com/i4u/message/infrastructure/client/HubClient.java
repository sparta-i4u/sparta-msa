package com.i4u.message.infrastructure.client;

import com.i4u.common.utils.CommonResponse;
import com.i4u.message.infrastructure.dto.HubDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "HUB-SERVICE")
public interface HubClient {
    
    @GetMapping("/api/v1/hubs/{hubId}")
    CommonResponse<HubDto> getHubById(@PathVariable("hubId") UUID hubId);
}
