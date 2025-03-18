package com.i4u.delivery.presentation.dtos.request;

import java.util.UUID;

import com.i4u.delivery.domain.entity.Delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryShipperCreateRequest {

	// 배송 담당자 ID 배정 받아오기 (업체 배송 담당자)
	// 업체 담당 허브 ID로 받아와야 함
	private UUID recipientHubId;

}
