package com.i4u.hub.presentation.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.hub.application.service.HubClientService;
import com.i4u.hub.presentation.dtos.request.DeliveryHubUpdateRequest;
import com.i4u.hub.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.hub.presentation.dtos.response.DeliveryHubUpdateResponse;
import com.i4u.hub.presentation.dtos.response.ShipperHubResponse;
import com.i4u.hub.presentation.dtos.request.DeliveryHubCreateRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubEndpoint {

	private final HubClientService hubClientService;

	/**
	 * 배송 담당자 측에서 보내는 허브 검증 요청 (By Gateway)
	 *
	 * @param hubId : 검증을 요청한 허브 ID
	 * @return : 검증한 내용
	 */
	@GetMapping("/shippers/{hubId}")
	public Mono<Map<String, Object>> confirmHubFromShippers(@PathVariable UUID hubId) {
		Map<String, Object> response = hubClientService.confirmHubFromShippers(hubId);
		return Mono.just(response);
	}

	/**
	 * 배송 담당자 측에서 보내는 허브 검증 요청
	 *
	 * @param hubId : 검증을 요청한 HubId
	 * @return : 검증이 완료된 정보
	 */
	@GetMapping("/{hubId}/shipper")
	ResponseEntity<CommonResponse<ShipperHubResponse>> confirmHubFromShipper(@PathVariable UUID hubId) {
		ShipperHubResponse response = hubClientService.confirmHubFromShipper(hubId);
		return ResponseEntity.ok(CommonResponse.success(response, "허브 검증 완료"));
	}

	@GetMapping("/deliveries/confirm")
	ResponseEntity<CommonResponse<DeliveryHubCreateResponse>> confirmHubsFromDelivery(@ModelAttribute DeliveryHubCreateRequest request) {
		DeliveryHubCreateResponse response = hubClientService.confirmHubsFromDelivery(request);
		return ResponseEntity.ok(CommonResponse.success(response, ""));
	}

	@GetMapping("/confirm-update")
	ResponseEntity<CommonResponse<DeliveryHubUpdateResponse>> updateConfirmHubsFromDelivery(DeliveryHubUpdateRequest request) {
		DeliveryHubUpdateResponse response = hubClientService.updateConfirmHubsFromDelivery(request);
		return ResponseEntity.ok(CommonResponse.success(response, ""));
	}

}
