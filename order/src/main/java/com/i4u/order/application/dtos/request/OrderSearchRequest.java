package com.i4u.order.application.dtos.request;

import java.util.UUID;

import com.i4u.order.domain.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchRequest {

	// 요청 업체 ID
	private UUID supplierId;

	// 수령 업체 ID
	private UUID recipientId;

	// 상품 ID
	private UUID productId;

	// 주문 상태
	private OrderStatus orderStatus;

}
