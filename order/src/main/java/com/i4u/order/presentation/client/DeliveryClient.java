package com.i4u.order.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderDeliveryResponse;
import com.i4u.order.presentation.dtos.response.OrderDeliveryUpdateResponse;

@FeignClient(name = "delivery")
public interface DeliveryClient {

	@PostMapping("/api/v1/deliveries/order")
	ResponseEntity<CommonResponse<OrderDeliveryResponse>> createDelivery(@RequestBody OrderDeliveryRequest request);

	@PutMapping("/api/v1/deliveries/order/{orderId}/update")
	ResponseEntity<CommonResponse<OrderDeliveryUpdateResponse>> updateDelivery(@RequestBody OrderDeliveryUpdateRequest request);

	@PatchMapping("/api/v1/deliveries/order/{orderId}/updateOrderState")
	ResponseEntity<CommonResponse> updateDeliveryState(@RequestBody OrderDeliveryStateUpdateRequest orderCanceled);

}