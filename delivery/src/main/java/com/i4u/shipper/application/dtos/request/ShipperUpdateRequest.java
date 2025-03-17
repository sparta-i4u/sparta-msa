package com.i4u.shipper.application.dtos.request;

import java.util.UUID;

import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.entity.ShipperType;

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
	public ShipperType shipperType;

	public Shipper toShipper(Integer shipperOrder, UUID hubId) {
		return Shipper.builder()
			.hubId(hubId)
			.shipperOrder(shipperOrder)
			.shipperType(this.shipperType)
			.build();
	}

}
