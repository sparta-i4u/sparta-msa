package com.i4u.hub.presentation.endpoint;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.hub.application.service.HubClientService;
import com.i4u.hub.presentation.dtos.ShipperHubResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubEndpoint {

	private final HubClientService hubClientService;

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

}
