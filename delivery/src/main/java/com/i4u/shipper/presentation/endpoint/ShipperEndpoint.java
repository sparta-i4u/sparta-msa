package com.i4u.shipper.presentation.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.application.service.ShipperClientService;
import com.i4u.shipper.presentation.dtos.request.DeliveryShipperRequest;
import com.i4u.shipper.presentation.dtos.response.DeliveryShipperResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShipperEndpoint {

	private final ShipperClientService shipperClientService;
	
	@GetMapping("/api/v1/deliveries/shippers")
	ResponseEntity<CommonResponse<DeliveryShipperResponse>> assignShipper(@ModelAttribute DeliveryShipperRequest request) {
		DeliveryShipperResponse response = shipperClientService.assignShipper(request);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 지정 성공"));
	}

}
