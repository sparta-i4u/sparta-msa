package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.i4u.delivery.presentation.dtos.request.DeliveryHubCreateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryHubUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubUpdateResponse;

@FeignClient(name = "hub-service")
public interface HubClient {

	@GetMapping("/api/v1/deliveries/hubs/confirm")
	DeliveryHubCreateResponse confirmHubs(DeliveryHubCreateRequest build);

	@GetMapping("/api/v1/deliveries/hubs/confirm/update")
	DeliveryHubUpdateResponse updateHubInfo(DeliveryHubUpdateRequest build);
}
