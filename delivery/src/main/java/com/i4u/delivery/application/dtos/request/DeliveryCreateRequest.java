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
public class DeliveryCreateRequest {

	// 주문 ID
	private UUID orderId;

	// 출발 허브 ID (배송하는 업체의 허브 ID를 받아와야 함)
	private UUID departHubId;

	// 도착 허브 ID (배송받는 업체의 허브 ID를 받아와야 함)
	private UUID arriveHubId;

	// 배송지 주소
	private String address;

	// 수령자 ID (배송받는 업체에서 주문한 사람의 ID 필요)
	private UUID recipientId;

	public Delivery toDelivery(DeliveryState deliveryState, String recipientSlackId, UUID shipperIc) {
		return Delivery.builder()
			.orderId(this.orderId)
			.deliveryState(deliveryState)
			.departHubId(this.departHubId)
			.arriveHubId(this.arriveHubId)
			.address(this.address)
			.recipientId(this.recipientId)
			.recipientSlackId(recipientSlackId)
			.shipperId(shipperIc)
			.build();
	}

}
