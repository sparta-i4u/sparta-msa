package com.i4u.order.domain.repository;

import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PagedModel;

import com.i4u.order.application.dtos.request.OrderSearchRequest;
import com.i4u.order.application.dtos.response.OrderGetListResponse;

public interface OrderRepositoryCustom {

	PagedModel<OrderGetListResponse> searchOrder(Pageable pageable, OrderSearchRequest request);
}
