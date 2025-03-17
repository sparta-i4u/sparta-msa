package com.i4u.shipper.presentation.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.shipper.presentation.dtos.request.ShipperHubRequest;
import com.i4u.shipper.presentation.dtos.response.ShipperHubResponse;

@FeignClient(name = "hub")
public interface HubClient {

	// Shipper -> Hub 로 해당 허브가 존재하는지 여부 확인
	@GetMapping("/hubs/{hubId}")
	ShipperHubResponse confirmHub(@ModelAttribute ShipperHubRequest request /*userId, userRole or JWT 필요*/);

}
