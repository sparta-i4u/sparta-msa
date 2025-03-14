package com.i4u.shipper.presentation.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hub")
public interface HubClient {

	// Shipper -> Hub 로 해당 허브가 존재하는지 여부 확인 (Boolean ?)
	@GetMapping("/hubs/{hubId}")
	Boolean getHubInfo(@PathVariable("hubId") UUID hubId);

}
