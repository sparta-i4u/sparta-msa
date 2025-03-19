package com.i4u.delivery.application.dtos.response;

import java.util.UUID;

import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryGetListResponse {

	private UUID deliveryId;

	private UUID orderId;

	private DeliveryState deliveryState;

	private UUID departHubId;

	private UUID arriveHubId;

	private String address;

	private UUID recipientId;

	private String recipientSlackId;

	private UUID shipperId;

	public static DeliveryGetListResponse fromDelivery(Delivery delivery) {
		return DeliveryGetListResponse.builder()
			.deliveryId(delivery.getDeliveryId())
			.orderId(delivery.getOrderId())
			.deliveryState(delivery.getDeliveryState())
			.departHubId(delivery.getDepartHubId())
			.arriveHubId(delivery.getArriveHubId())
			.address(delivery.getAddress())
			.recipientId(delivery.getRecipientId())
			.recipientSlackId(delivery.getRecipientSlackId())
			.shipperId(delivery.getShipperId())
			.build();
	}

}
