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
public class ShipperCreateRequest {

	// 배송 담당자가 속한 허브 ID
	public UUID hubId;

	// 배송 담당자 타입 (허브/업체)
	// 허브 배송 담당자면 hubId가 null 혹은 공백, 
	// 업체 배송 담당자면 hubId가 존재
	public ShipperType shipperType;

	// 배송 담당자의 사용자 ID
	public UUID userId;

	public Shipper toShipper(Integer shipperOrder, UUID hubId, String shipperSlackId) {
		return Shipper.builder()
			.shipperId(this.userId)
			.hubId(hubId)
			.shipperType(this.shipperType)
			.shipperOrder(shipperOrder)
			.userId(this.userId)
			.userSlackId(shipperSlackId)
			.build();
	}
}
