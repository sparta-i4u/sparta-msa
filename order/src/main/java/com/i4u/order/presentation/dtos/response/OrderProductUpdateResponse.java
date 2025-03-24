package com.i4u.order.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductUpdateResponse {

	// 이후 상품이 없으면 Product 측에서 isDeleted를 true로 주고,
	// 이전 상품의 재고를 +해줄 필요 X
	private UUID afterProductId;
	private Integer afterProductQuantity;
	private Integer afterProductToTalPrice;
	private Boolean isDeleted;

}
