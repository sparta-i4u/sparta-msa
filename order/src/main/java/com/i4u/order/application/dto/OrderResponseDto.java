package com.i4u.order.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponseDto {

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

	public static OrderResponseDto getSampleDto() {
		return OrderResponseDto.builder()
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

	public static OrderResponseDto createSampleDto(OrderCreateRequestDto orderCreateRequestDto) {
		return OrderResponseDto.builder()
			.orderId(UUID.randomUUID())
			.supplierId(orderCreateRequestDto.getSupplierId())
			.recipientId(orderCreateRequestDto.getRecipientId())
			.productId(orderCreateRequestDto.getProductId())
			.productQuantity(orderCreateRequestDto.getProductQuantity())
			.requirement(orderCreateRequestDto.getRequirement())
			.address(orderCreateRequestDto.getAddress())
			.deliveryId(UUID.randomUUID())
			.deliveryState("결제 완료")
			.build();
	}


	public static OrderResponseDto updateSampleDto(OrderUpdateRequestDto orderUpdateRequestDto) {
		return OrderResponseDto.builder()
			.orderId(UUID.randomUUID())
			.supplierId(UUID.randomUUID())
			.recipientId(orderUpdateRequestDto.getRecipientId())
			.productId(orderUpdateRequestDto.getProductId())
			.productQuantity(orderUpdateRequestDto.getProductQuantity())
			.requirement(orderUpdateRequestDto.getRequirement())
			.address(orderUpdateRequestDto.getAddress())
			.deliveryId(UUID.randomUUID())
			.deliveryState("결제 완료")
			.build();
	}

	public static OrderResponseDto updateStatusSampleDto(OrderStatusUpdateRequestDto orderUpdateRequestDto) {
		return OrderResponseDto.builder()
			.orderId(UUID.randomUUID())
			.supplierId(UUID.randomUUID())
			.recipientId(UUID.randomUUID())
			.productId(UUID.randomUUID())
			.productQuantity(10)
			.requirement("2025년 3월 22일까지는 보내주세요.")
			.address("인천시 계양구")
			.deliveryId(UUID.randomUUID())
			.deliveryState(orderUpdateRequestDto.getOrderStatus())
			.build();
	}
}
