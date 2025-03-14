package com.i4u.order.application.dtos.request;

import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderStatusUpdateRequest {

	// 변경할 주문의 상태
	public OrderStatus orderStatus;

	public Order toOrder() {
		return Order.builder()
			.orderStatus(this.orderStatus)
			.build();
	}

}
