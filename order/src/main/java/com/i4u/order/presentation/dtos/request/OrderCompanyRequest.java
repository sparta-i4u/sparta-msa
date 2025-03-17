package com.i4u.order.presentation.dtos.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompanyRequest {

	// 요청 업체 ID (요청을 받은 거임 (이 업체의 상품을 주문할 것))
	private UUID supplierId;

	// 수령 업체 ID (주문을 한 거임)
	private UUID recipientId;
}
