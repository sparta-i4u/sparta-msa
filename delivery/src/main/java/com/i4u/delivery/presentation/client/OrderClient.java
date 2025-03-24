package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.i4u.delivery.presentation.dtos.request.DeliveryOrderStateUpdateRequest;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

	// 주문 측에서는 이 API를 받으면 주문 상태를 배송 취소/삭제 등으로 변경하기

	@PatchMapping("/api/v1/orders/deliveries/noti-update")
	void notificationDeliveryState(@RequestBody DeliveryOrderStateUpdateRequest request);

}