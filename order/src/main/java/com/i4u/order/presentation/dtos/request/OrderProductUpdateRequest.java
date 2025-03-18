package com.i4u.order.presentation.dtos.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductUpdateRequest {

	private UUID beforeProductId;
	private Integer beforeProductQuantity;
	private UUID afterProductId;
	private Integer afterProductQuantity;

}