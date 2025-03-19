package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderDeleteRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderStateUpdateRequest;

@FeignClient(name = "order-service")
public interface OrderClient {

	// 주문 측에서는 이 API를 받으면 주문 상태를 배송 취소/삭제 등으로 변경하기

	@PatchMapping("/api/v1/deliveries/orders/noti-update")
	ResponseEntity<CommonResponse> notificationDeliveryState(@RequestBody DeliveryOrderStateUpdateRequest request);

}