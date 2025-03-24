package com.i4u.hub.presentation.endpoint;

import java.util.Map;
import java.util.UUID;

import com.i4u.hub.domain.model.Hub;
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
import com.i4u.hub.presentation.dtos.response.HubDto;
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
	 * 허브 담당자가 관리하는 허브 ID 반환 (소속 검증)
	 *
	 * @param userId : 검증을 요청할 사용자
	 * @return : 허브 관리자의 허브 ID 반환
	 */
	@GetMapping("/shippers/{userId}")
	UUID confirmHubFromUser(@PathVariable UUID userId) {
		UUID hubId = hubClientService.confirmHubFromUser(userId);
		return hubId;
	}

	// @GetMapping("/{hubId}/shipper")
	// ResponseEntity<CommonResponse<ShipperHubResponse>> confirmHubFromShipper(@PathVariable UUID hubId) {
	// 	ShipperHubResponse response = hubClientService.confirmHubFromShipper(hubId);
	// 	return ResponseEntity.ok(CommonResponse.success(response, "허브 검증 완료"));
	// }

	/**
	 * Delivery에서 보내는 허브 검증 요청 (create)
	 *
	 * @param recipientHubId : 수령 허브
	 * @param supplierHubId : 공급 허브
	 * @return
	 */
	@GetMapping("/deliveries/{recipientHubId}/{supplierHubId}")
	DeliveryHubCreateResponse confirmHubsFromDelivery(@PathVariable UUID recipientHubId, @PathVariable UUID supplierHubId) {
		DeliveryHubCreateResponse response = hubClientService.confirmHubsFromDelivery(recipientHubId, supplierHubId);
		return response;
	}

	/**
	 * Delivery에서 보내는 허브 검증 요청 (update)
	 *
	 * @param recipientHubId : 공급 허브
	 * @return
	 */
	@GetMapping("/deliveries/{supplierHubId}")
	ResponseEntity<CommonResponse<DeliveryHubUpdateResponse>> updateConfirmHubsFromDelivery(@PathVariable UUID recipientHubId) {
		DeliveryHubUpdateResponse response = hubClientService.updateConfirmHubsFromDelivery(recipientHubId);
		return ResponseEntity.ok(CommonResponse.success(response, ""));
	}

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

	@GetMapping("/messges/{supplierHubId}/{recipientHubId}")
	public CommonResponse<HubDto> getHubInfos(@PathVariable UUID supplierHubId, @PathVariable UUID recipientHubId) {
		HubDto response = hubClientService.confirmHubFromMessages(supplierHubId, recipientHubId);
		return CommonResponse.success(response, "성공");
	}

	//product가 보내는 hubId 검증 요청
//	@GetMapping("/api/v1/hubs/products/{hubId}")
//	public UUID getHubIdByProduct(@PathVariable UUID hubId);
	@GetMapping("/products/{hubId}")
	public Boolean confirmHubFromProduct(@PathVariable UUID hubId) {
		Boolean response = hubClientService.confirmHubFromProduct(hubId);
		return response;
	}


	//company가 보내는 로그인 한 사람이 허브매니저라면 본인 허브인지 확인
	//@GetMapping("/api/v1/hubs/companies/{userId}")
	//UUID getHubInfo(@PathVariable("userId") UUID userId);
	@GetMapping("/companies/{hubId}")
	public UUID getHubInfo(@PathVariable UUID hubId) {
		Hub response = hubClientService.getHubInfoFromCompany(hubId);
		if(response == null) {
			return null;
		}
		return hubId;
	}

	//company가 보내는 hubId 검증
	//@GetMapping("/api/v1/hubs/companies/checkHubId/{hubId}")
	//Boolean getHubId(@PathVariable("hubId") UUID hubId);
	@GetMapping("/companies/checkHubId/{hubId}")
	public Boolean getHubId(@PathVariable UUID hubId) {
		Boolean response = hubClientService.confirmHubFromCompany(hubId);
		return response;
	}
}
