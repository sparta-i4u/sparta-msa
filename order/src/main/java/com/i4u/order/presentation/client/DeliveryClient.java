package com.i4u.order.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderDeliveryResponse;
import com.i4u.order.presentation.dtos.response.OrderDeliveryUpdateResponse;

@FeignClient(name = "delivery")
public interface DeliveryClient {

	@GetMapping("/deliveries/{orderId}")
	OrderDeliveryResponse createDelivery(@ModelAttribute OrderDeliveryRequest request);

	@GetMapping("/deliveries/{orderId}/updateOrderInfo")
	OrderDeliveryUpdateResponse updateDelivery(@ModelAttribute OrderDeliveryUpdateRequest request);

	@GetMapping("/deliveries/{orderId}/updateOrderState")
	void updateDeliveryState(OrderDeliveryStateUpdateRequest orderCanceled);
}
