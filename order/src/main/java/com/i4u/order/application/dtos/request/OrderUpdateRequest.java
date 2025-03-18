package com.i4u.order.application.dtos.request;

import java.util.UUID;

import com.i4u.order.domain.entity.Order;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderUpdateRequest {

	// 요청(공급) 업체 ID (companyId) - 타 업체의 상품 주문으로 수정하고 싶을 때
	public UUID supplierId;

	// 주문할 상품의 ID
	public UUID productId;

	// 주문할 상품의 수량
	@Min(1)
	public Integer productQuantity;

	// 요청 사항
	public String requirement;

	public Order toOrder(Long productTotalPrice) {
		return Order.builder()
			.supplierId(this.supplierId)
			.productId(this.productId)
			.productQuantity(this.productQuantity)
			.productTotalPrice(productTotalPrice)
			.requirement(this.requirement)
			.build();
	}

}
