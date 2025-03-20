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
public class OrderDeliveryUpdateResponse {

	// 생성된 delivery ID
	private UUID deliveryId;

	// 배송 상태
	private String deliveryState;

}
