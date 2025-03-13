package com.i4u.shipper.application.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ShipperResponse {

	// 배송 담당자 ID
	public UUID shipperId;

	// 배송 담당자가 속한 허브 ID
	public UUID hubId;

	// 배송 담당자 타입 (허브/업체)
	public String shipperType;

	// 배송 담당자의 배송 순번
	public Integer shipperOrder;

	// 배송 담당자의 사용자 ID
	public UUID userId;

	public static ShipperResponse createDto(ShipperCreateRequest shipperCreateRequest) {
		return ShipperResponse.builder()
			.shipperId(UUID.randomUUID())
			.hubId(shipperCreateRequest.getHubId())
			.shipperType(shipperCreateRequest.getShipperType())
			.shipperOrder(1)
			.userId(shipperCreateRequest.getUserId())
			.build();
	}

	public static ShipperResponse getDto() {
		return ShipperResponse.builder()
			.shipperId(UUID.randomUUID())
			.hubId(UUID.randomUUID())
			.shipperType("허브 배송 담당자")
			.shipperOrder(1)
			.userId(UUID.randomUUID())
			.build();
	}

	public static ShipperResponse updateDto(ShipperUpdateRequest shipperUpdateRequest) {
		return ShipperResponse.builder()
			.shipperId(UUID.randomUUID())
			.hubId(shipperUpdateRequest.getHubId())
			.shipperType(shipperUpdateRequest.getShipperType())
			.shipperOrder(2)
			.userId(UUID.randomUUID())
			.build();
	}

}
