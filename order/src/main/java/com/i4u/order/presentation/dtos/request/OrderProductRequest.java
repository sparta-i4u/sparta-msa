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
public class OrderProductRequest {

	private UUID productId;
	private Integer productQuantity;

}
