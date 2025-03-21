package com.i4u.delivery.presentation.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.service.DeliveryClientService;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeliveryEndpoint {

	private final DeliveryClientService deliveryClientService;

	@PutMapping("/api/v1/deliveries/update-byorder")
	ResponseEntity<CommonResponse> updateDeliveryByOrder(@RequestBody OrderDeliveryUpdateRequest request){
		deliveryClientService.updateDeliveryByOrder(request);
		return ResponseEntity.ok(CommonResponse.success("", "배송 수정 성공"));
	}

	@PatchMapping("/api/v1/deliveries/updatestate-byorder")
	ResponseEntity<CommonResponse> updateDeliveryState(@RequestBody OrderDeliveryStateUpdateRequest orderCanceled) {
		deliveryClientService.updateDeliveryStateByOrder(orderCanceled);
		return ResponseEntity.ok(CommonResponse.success("", "배송 상태 수정 성공"));
	}

}
