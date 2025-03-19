package com.i4u.delivery.application.dtos.request;

import java.util.UUID;

import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryUpdateRequest {

	// 배송지 주소
	private String address;

	// 변경된 주소지에 맞는 HubId
	private UUID arriveHubId;

	// 수령인 ID
	private UUID recipientId;

	// 수령인 Slack ID
	private String recipientSlackId;

	public Delivery toDelivery(UUID recipientHubId, UUID shipperId, String userSlackId) {
		return Delivery.builder()
			.arriveHubId(recipientHubId)
			.shipperId(shipperId)
			.address(this.address)
			.recipientId(this.recipientId)
			.recipientSlackId(userSlackId)
			.build();
	}

}
