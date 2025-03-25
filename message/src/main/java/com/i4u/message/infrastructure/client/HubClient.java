package com.i4u.message.infrastructure.client;

import com.i4u.common.utils.CommonResponse;
import com.i4u.message.infrastructure.dto.HubDto;
import com.i4u.message.infrastructure.dto.ShortestPathResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "HUB-SERVICE")
public interface HubClient {
    
    @GetMapping("/api/v1/hubs/{hubId}")
    CommonResponse<HubDto> getHubById(@PathVariable("hubId") UUID hubId);

    @GetMapping("/api/v1/hubs/messges/{supplierHubId}/{recipientHubId}")
    CommonResponse<HubDto> getHubInfos(@PathVariable UUID supplierHubId, @PathVariable UUID recipientHubId);

    @GetMapping("/api/v1/hub-connections/shortest-path")
    ResponseEntity<CommonResponse<ShortestPathResDto>> getShortestPath(
            @RequestParam String startHubName,
            @RequestParam String endHubName);

}
