package com.i4u.shipper.application.dtos.response;

import java.util.UUID;

import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.entity.ShipperType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperListResponse {

	// 배송 담당자 ID
	public UUID shipperId;

	// 배송 담당자가 속한 허브 ID
	public UUID hubId;

	// 배송 담당자 타입 (허브/업체)
	public ShipperType shipperType;

	// 배송 담당자의 배송 순번
	public Integer shipperOrder;

	// 배송 담당자의 사용자 ID
	public UUID userId;

	public static ShipperListResponse fromShipper(Shipper shipper) {
		return ShipperListResponse.builder()
			.shipperId(shipper.getShipperId())
			.hubId(shipper.getHubId())
			.shipperType(shipper.getShipperType())
			.shipperOrder(shipper.getShipperOrder())
			.userId(shipper.getUserId())
			.build();
	}

}
