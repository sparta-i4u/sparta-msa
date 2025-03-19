package com.i4u.delivery.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryShipperResponse {

	private UUID recipientHubId;
	private UUID shipperId;
	private Boolean isDeleted;

}
