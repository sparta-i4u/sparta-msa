package com.i4u.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubUpdateResponse;

@FeignClient(name = "HUB-SERVICE")
public interface HubClient {

	// 요청을 보낸 사용자 (허브 담당자의 경우) 담당하는 허브의 ID 가 무엇인지 / 없으면 NULL
	@GetMapping("/api/v1/hubs/shippers/{userId}")
	UUID confirmHubFromUser(@PathVariable UUID userId);

	// Shipper -> Hub 로 해당 허브가 존재하는지 여부 확인 (생성/수정 시)
	// @GetMapping("/api/v1/hubs/{hubId}/shippers")
	// ResponseEntity<CommonResponse<ShipperHubResponse>> confirmHub(@PathVariable UUID hubId);

	// Delivery -> Hub로 해당 허브들이 존재하는지 확인
	@GetMapping("/api/v1/hubs/deliveries/{recipientHubId}/{supplierHubId}")
    DeliveryHubCreateResponse confirmHubsFromDelivery(@PathVariable UUID recipientHubId, @PathVariable UUID supplierHubId);

	// Delivery -> Hub로 해당 허브가 존재하는지 확인 (update 시 적용)
	@GetMapping("/api/v1/hubs/deliveries/{arriveHubId}")
	ResponseEntity<CommonResponse<DeliveryHubUpdateResponse>> updateConfirmHubsFromDelivery(@PathVariable UUID arriveHubId);


}
