package com.i4u.shipper.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;
import com.i4u.shipper.domain.entity.Shipper;

public interface ShipperRepositoryCustom {
	Page<ShipperListResponse> searchShippers(Pageable pageable, ShipperSearchRequest request,
		String userId, String role, UUID hubManagerHubId);
	Integer confirmShipperOrder(UUID hubId);
	Shipper assignShipper(UUID hubId);
	Shipper assignNewShipper(UUID hubId, Integer shipperOrder);
}
