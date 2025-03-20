package com.i4u.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.presentation.dtos.request.DeliveryHubCreateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryHubUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubUpdateResponse;
import com.i4u.shipper.presentation.dtos.response.ShipperHubResponse;

@FeignClient(name = "hub-service")
public interface HubClient {

	// 요청을 보낸 사용자 (허브 담당자의 경우) 담당하는 허브의 ID 가 무엇인지 / 없으면 NULL
	@GetMapping("/api/v1/hubs/shippers/{userId}")
	UUID confirmHubFromUser(@PathVariable UUID uuid);

	// Shipper -> Hub 로 해당 허브가 존재하는지 여부 확인
	@GetMapping("/api/v1/hubs/{hubId}/shippers")
	ResponseEntity<CommonResponse<ShipperHubResponse>> confirmHub(@PathVariable UUID hubId);

	@GetMapping("/api/v1/hubs/deliveries/confirm")
	ResponseEntity<CommonResponse<DeliveryHubCreateResponse>> confirmHubsFromDelivery(@ModelAttribute DeliveryHubCreateRequest request);

	@GetMapping("/api/v1/hubs/deliveries/confirm-update")
	ResponseEntity<CommonResponse<DeliveryHubUpdateResponse>> updateConfirmHubsFromDelivery(@ModelAttribute DeliveryHubUpdateRequest request);


}
