package com.i4u.order.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderUpdateRequestDto {

	// 수령 업체 ID (companyId) - 타 업체의 상품 주문으로 수정하고 싶을 때
	public UUID recipientId;

	// 주문할 상품의 ID
	public UUID productId;
	// 주문할 상품의 수량
	public Integer productQuantity;

	// 요청 사항
	public String requirement;

	// 배송지
	public String address;

	public static OrderUpdateRequestDto createSampleDto() {
		return OrderUpdateRequestDto.builder()
			.recipientId(UUID.randomUUID())
			.productId(UUID.randomUUID())
			.productQuantity(20)
			.requirement("2025년 3월 20일까지는 보내주세요.")
			.address("인천시 서구")
			.build();
	}

}
