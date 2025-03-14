package com.i4u.order.application.dtos.response;

import java.util.UUID;

import com.i4u.order.domain.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderCreateResponse {

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

	// 배송 주소
	public String address;

	// 주문 상태
	public String orderState;

	public static OrderCreateResponse toDto(Order order) {
		// 생성 직후에는 Delivery ID 없이 반환
		return OrderCreateResponse.builder()
			.orderId(order.getOrderId())
			.supplierId(order.getSupplierId())
			.recipientId(order.getRecipientId())
			.productId(order.getProductId())
			.productQuantity(order.getProductQuantity())
			.requirement(order.getRequirement())
			.address(order.getAddress())
			.orderState(order.getOrderStatus().toString())
			.build();
	}
}
