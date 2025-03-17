package com.i4u.order.application.dtos.request;

import java.util.UUID;

import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderCreateRequest {

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

	public Order toOrder(Long productTotalPrice/*, UUID userId*/) {
		return Order.builder()
			.supplierId(this.supplierId)
			.recipientId(this.recipientId)
			.productId(this.productId)
			.productQuantity(this.productQuantity)
			.productTotalPrice(productTotalPrice)
			.requirement(this.requirement)
			.orderStatus(OrderStatus.PAID)  // 생성 시에는 결제 완료 상태로 주문 생성
			// .userId(userId)
			.build();
	}
}
