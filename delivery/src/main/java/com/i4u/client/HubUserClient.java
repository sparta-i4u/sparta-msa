package com.i4u.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import reactor.core.publisher.Mono;

@FeignClient(path = "/api/v1/gateway")
public interface HubUserClient {

	@GetMapping("/shipper-info/{userId}/{hubId}")
	Mono<Map<String, Object>> getShipperInfo(@PathVariable UUID userId, @PathVariable UUID hubId);

	@GetMapping("/shipper-info/{hubId}")
	Mono<Map<String, Object>> getShipperInfoFromHub(@PathVariable UUID hubId);

}
