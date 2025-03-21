package com.i4u.order.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderDeliveryResponse;

@FeignClient(name = "DELIVERY-SERVICE")
public interface DeliveryClient {

	// 배송 controller로 요청 전송
	@PostMapping("/api/v1/deliveries")
	ResponseEntity<CommonResponse<OrderDeliveryResponse>> createDelivery(@RequestBody OrderDeliveryRequest request);

	// 배송 Endpoint로 요청 전송
	@PutMapping("/api/v1/deliveries/update-byorder")
	ResponseEntity<CommonResponse> updateDeliveryByOrder(@RequestBody OrderDeliveryUpdateRequest request);

	// 배송 Endpoint로 요청 전송
	@PatchMapping("/api/v1/deliveries/updatestate-byorder")
	ResponseEntity<CommonResponse> updateDeliveryStateByOrder(@RequestBody OrderDeliveryStateUpdateRequest orderCanceled);

}