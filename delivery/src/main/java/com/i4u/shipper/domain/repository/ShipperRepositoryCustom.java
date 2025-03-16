package com.i4u.shipper.domain.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;

public interface ShipperRepositoryCustom {
	PagedModel<ShipperListResponse> searchShippers(Pageable pageable, ShipperSearchRequest request/*, String role*/);
}
