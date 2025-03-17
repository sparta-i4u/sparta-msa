package com.i4u.shipper.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;

public interface ShipperRepositoryCustom {
	Page<ShipperListResponse> searchShippers(Pageable pageable, ShipperSearchRequest request/*, String role*/);
	Integer confirmShipperOrder(UUID hubId);
}
