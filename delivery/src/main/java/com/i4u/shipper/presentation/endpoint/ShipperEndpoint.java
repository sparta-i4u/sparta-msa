package com.i4u.shipper.presentation.endpoint;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.application.service.ShipperClientService;
import com.i4u.shipper.presentation.dtos.request.DeliveryShipperRequest;
import com.i4u.shipper.presentation.dtos.response.DeliveryShipperResponse;

import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/shippers")
@RequiredArgsConstructor
public class ShipperEndpoint {

	private final ShipperClientService shipperClientService;
	
	// @GetMapping("/deliveries/{recipientHubId}")
	// ResponseEntity<CommonResponse<DeliveryShipperResponse>> assignShipper(@PathVariable UUID recipientHubId) {
	// 	DeliveryShipperResponse response = shipperClientService.assignShipper(recipientHubId);
	// 	return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 지정 성공"));
	// }

}
