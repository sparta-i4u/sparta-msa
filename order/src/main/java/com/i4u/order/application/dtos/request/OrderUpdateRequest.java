package com.i4u.order.application.dtos.request;

import java.util.UUID;

import com.i4u.order.domain.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderUpdateRequest {

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

	public Order toOrder() {
		return Order.builder()
			.recipientId(this.recipientId)
			.productId(this.productId)
			.productQuantity(this.productQuantity)
			.requirement(this.requirement)
			.address(this.address)
			.build();
	}

}
