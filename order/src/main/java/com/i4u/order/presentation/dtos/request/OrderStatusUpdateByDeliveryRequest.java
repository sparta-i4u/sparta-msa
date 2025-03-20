package com.i4u.order.presentation.dtos.request;

import java.util.UUID;

import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateByDeliveryRequest {

	private UUID orderId;
	private UUID deliveryId;
	private String deliveryState;

	public Order toOrder(OrderStatus orderStatus) {
		return Order.builder()
			.orderStatus(orderStatus)
			.build();
	}

}
