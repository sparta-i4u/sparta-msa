package com.i4u.product.application.dto.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductResponse {

	// 상품의 재고가 부족하거나, 상품이 없는 경우 true
	private Boolean isDeleted;
	// 상품 ID
	private UUID productId;
	// 상품 수량
	private Integer productQuantity;
	// 총 주문 가격
	private Long productTotalPrice;

}
