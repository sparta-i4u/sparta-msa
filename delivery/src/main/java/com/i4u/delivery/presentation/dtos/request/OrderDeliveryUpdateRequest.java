package com.i4u.delivery.presentation.dtos.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryUpdateRequest {

	// 주문 ID
	private UUID orderId;

	// 요청 업체 HubId
	private UUID supplierHubId;

}