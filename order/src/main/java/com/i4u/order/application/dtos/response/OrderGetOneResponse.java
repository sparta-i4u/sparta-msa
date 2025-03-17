package com.i4u.order.application.dtos.response;

import java.util.UUID;

import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderGetOneResponse {

	// 주문 ID
	public UUID orderId;

	// 요청 업체 ID (companyId)
	public UUID supplierId;
	// 수령 업체 ID (companyId)
	public UUID recipientId;

	// 주문할 상품의 ID
	public UUID productId;
	// 주문할 상품의 수량
	public Integer productQuantity;

	// 요청 사항
	public String requirement;

	// 배송 ID
	public UUID deliveryId;

	// 주문 상태
	public OrderStatus orderStatus;

	public static OrderGetOneResponse fromOrder(Order order) {
		return OrderGetOneResponse.builder()
			.orderId(order.getOrderId())
			.supplierId(order.getSupplierId())
			.recipientId(order.getRecipientId())
			.productId(order.getProductId())
			.productQuantity(order.getProductQuantity())
			.requirement(order.getRequirement())
			.deliveryId(order.getDeliveryId())
			.orderStatus(order.getOrderStatus())
			.build();
	}

}
