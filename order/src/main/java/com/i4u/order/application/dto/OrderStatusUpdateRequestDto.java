package com.i4u.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderStatusUpdateRequestDto {

	// 변경할 주문의 상태
	public String orderStatus;

}
