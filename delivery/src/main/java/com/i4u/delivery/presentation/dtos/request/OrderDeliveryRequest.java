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
public class OrderDeliveryRequest {

	// 주문 ID
	private UUID orderId;

	// 요청 업체 HubId
	private UUID supplierHubId;

	// 수령 업체 HubId
	private UUID recipientHubId;

	// 배송 주소
	private String address;

	// 주문한 사용자 (Delivery 측에서는 받아서 사용자 SlackID도 요청해서 저장하기)
	private UUID userId;


}
