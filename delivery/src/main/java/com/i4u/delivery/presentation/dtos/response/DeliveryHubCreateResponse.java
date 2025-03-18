package com.i4u.delivery.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryHubCreateResponse {

	private UUID supplierHubId;
	private UUID recipientHubId;
	private Boolean isDeleted;

}
