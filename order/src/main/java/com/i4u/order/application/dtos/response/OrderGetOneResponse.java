package com.i4u.order.application.dtos.response;

import java.util.UUID;

import com.i4u.order.domain.entity.Order;

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

	// 배송 주소
	public String address;

	// 배송 ID
	public UUID deliveryId;

	// 주문 상태
	public String deliveryState;

	public static OrderGetOneResponse toDto(Order order) {
		return OrderGetOneResponse.builder()
			.orderId(UUID.randomUUID())
			.supplierId(UUID.randomUUID())
			.recipientId(UUID.randomUUID())
			.productId(UUID.randomUUID())
			.productQuantity(10)
			.requirement("2025년 3월 22일까지는 보내주세요.")
			.address("인천시 계양구")
			.deliveryId(UUID.randomUUID())
			.deliveryState("결제 완료")
			.build();
	}

}
