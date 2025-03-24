package com.i4u.order.presentation.endpoint;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.order.application.service.OrderClientService;
import com.i4u.order.presentation.dtos.request.DeliveryOrderStateUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderEndpoint {

	private final OrderClientService orderClientService;

	/**
	 * 배송 상태 변경에 따른 주문 상태 변경
	 *
	 * @param request : 주문 상태를 변경할 내용
	 */
	@PatchMapping("/deliveries/noti-update")
	void notificationDeliveryState(@RequestBody DeliveryOrderStateUpdateRequest request) {
		orderClientService.notificationDeliveryState(request);
	}

}
