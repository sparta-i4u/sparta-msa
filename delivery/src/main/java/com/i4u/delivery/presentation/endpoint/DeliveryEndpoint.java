package com.i4u.delivery.presentation.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.service.DeliveryClientService;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.OrderDeliveryResponse;
import com.i4u.delivery.presentation.dtos.response.OrderDeliveryUpdateResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeliveryEndpoint {

	private final DeliveryClientService deliveryClientService;

	@PostMapping("/api/v1/deliveries/order")
	ResponseEntity<CommonResponse<OrderDeliveryResponse>> createDelivery(@RequestBody OrderDeliveryRequest request) {
		OrderDeliveryResponse response = deliveryClientService.createDelivery(request);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 생성 성공"));
	}

	@PutMapping("/api/v1/deliveries/order/{orderId}/update")
	ResponseEntity<CommonResponse<OrderDeliveryUpdateResponse>> updateDelivery(@RequestBody OrderDeliveryUpdateRequest request){
		OrderDeliveryUpdateResponse response = deliveryClientService.updateDelivery(request);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 수정 성공"));
	}

	@PatchMapping("/api/v1/deliveries/order/{orderId}/updateOrderState")
	ResponseEntity<CommonResponse> updateDeliveryState(@RequestBody OrderDeliveryStateUpdateRequest orderCanceled) {
		deliveryClientService.updateDeliveryState(orderCanceled);
		return ResponseEntity.ok(CommonResponse.success("", "배송 수정 성공"));
	}



}
