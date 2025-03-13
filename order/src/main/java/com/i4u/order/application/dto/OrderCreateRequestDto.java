package com.i4u.order.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderCreateRequestDto {

	// 요청 업체 ID (companyId)
	public UUID supplierId;
	// 수령 업체 ID (companyId)
	public UUID recipientId;

	// 주문할 상품의 ID (List<UUID>)
	public UUID productId;
	// 주문할 상품의 수량
	public Integer productQuantity;

	// 요청 사항
	public String requirement;

	// 배송지
	public String address;

	public static OrderCreateRequestDto createSampleDto() {
		return OrderCreateRequestDto.builder()
			.supplierId(UUID.randomUUID())
			.recipientId(UUID.randomUUID())
			.productId(UUID.randomUUID())
			.productQuantity(10)
			.requirement("2025년 3월 22일까지는 보내주세요.")
			.address("인천시 계양구")
			.build();
	}
}
