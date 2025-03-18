package com.i4u.delivery.application.dtos.response;

import java.util.UUID;

import com.i4u.delivery.domain.entity.Delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryUpdateResponse {

	private UUID deliveryId;

	private UUID orderId;

	private String deliveryState;

	private UUID departHubId;

	private UUID arriveHubId;

	private String address;

	private UUID recipientId;

	private String recipientSlackId;

	private UUID shipperId;

	public static DeliveryUpdateResponse fromDelivery(Delivery delivery) {
		return DeliveryUpdateResponse.builder()
			.deliveryId(delivery.getDeliveryId())
			.orderId(delivery.getOrderId())
			.deliveryState(delivery.getDeliveryState().toString())
			.departHubId(delivery.getDepartHubId())
			.arriveHubId(delivery.getArriveHubId())
			.address(delivery.getAddress())
			.recipientId(delivery.getRecipientId())
			.recipientSlackId(delivery.getRecipientSlackId())
			.shipperId(delivery.getShipperId())
			.build();
	}

}
