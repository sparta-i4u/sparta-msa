package com.i4u.shipper.domain.repository;

import org.springframework.data.web.PagedModel;

import com.i4u.shipper.application.dtos.response.ShipperListResponse;

public interface ShipperRepositoryCustom {
	PagedModel<ShipperListResponse> searchShippers();
}
