package com.i4u.delivery.presentation.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryHubUpdateRequest {

	private String address;

}
