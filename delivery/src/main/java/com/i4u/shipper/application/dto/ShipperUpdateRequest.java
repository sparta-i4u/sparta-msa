package com.i4u.shipper.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShipperUpdateRequest {

	// 배송 담당자가 속한 허브 ID
	public UUID hubId;

	// 배송 담당자 타입 (허브/업체)
	public String shipperType;

}
