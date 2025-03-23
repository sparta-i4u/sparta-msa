package com.i4u.delivery.application.dtos.response;

import java.util.UUID;

import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryCreateResponse {

	// orderId
	private UUID orderId;

	// 생성된 delivery ID
	private UUID deliveryId;

	// 배송 상태
	private String deliveryState;

	public static DeliveryCreateResponse fromDelivery(Delivery delivery) {
		return DeliveryCreateResponse.builder()
			.orderId(delivery.getOrderId())
			.deliveryId(delivery.getDeliveryId())
			.deliveryState(delivery.getDeliveryState().toString())
			.build();
	}

	public static DeliveryCreateResponse fromDeliveryError(UUID orderId, String errorContent) {
		return DeliveryCreateResponse.builder()
			.orderId(orderId)
			.deliveryState(errorContent)
			.build();
	}

}
