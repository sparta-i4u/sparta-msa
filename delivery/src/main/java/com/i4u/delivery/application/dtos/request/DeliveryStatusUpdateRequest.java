package com.i4u.delivery.application.dtos.request;

import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryStatusUpdateRequest {

	// 수정할 배송 상태
	private DeliveryState deliveryState;

	// 배송 상태만 담은 Delivery 생성
	public Delivery toDelivery() {
		return Delivery.builder()
			.deliveryState(this.deliveryState)
			.build();
	}
}
