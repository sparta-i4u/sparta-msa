package com.i4u.order.presentation.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.application.service.OrderClientService;
import com.i4u.order.presentation.dtos.request.DeliveryOrderStateUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderEndpoint {

	private final OrderClientService orderClientService;

	/**
	 * 배송 상태 변경에 따른 주문 상태 변경
	 *
	 * @param request
	 * @return
	 */
	@PatchMapping("/api/v1/deliveries/orders/noti-update")
	ResponseEntity<CommonResponse> notificationDeliveryState(@RequestBody DeliveryOrderStateUpdateRequest request) {
		orderClientService.notificationDeliveryState(request);
		return ResponseEntity.ok(CommonResponse.success("", ""));
	}

}
