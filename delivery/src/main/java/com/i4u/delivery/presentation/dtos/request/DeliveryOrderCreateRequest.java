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
public class DeliveryOrderCreateRequest {

	private UUID orderId;
	private UUID deliveryId;
	private String deliveryState;

}
