package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.i4u.delivery.presentation.dtos.request.DeliveryHubCreateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryHubUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubUpdateResponse;

import lombok.Getter;

@FeignClient(name = "hub")
public interface HubClient {

	@GetMapping("/deliveries/{deliveryId}/hubs/confirm")
	DeliveryHubCreateResponse confirmHubs(DeliveryHubCreateRequest build);

	@GetMapping("")
	DeliveryHubUpdateResponse updateHubInfo(DeliveryHubUpdateRequest build);
}
