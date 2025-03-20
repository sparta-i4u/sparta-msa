package com.i4u.delivery.domain.repository;

import java.util.UUID;

import com.i4u.delivery.application.dtos.request.DeliverySearchRequest;
import com.i4u.delivery.application.dtos.response.DeliveryGetListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface DeliveryRepositoryCustom {
    PagedModel<DeliveryGetListResponse> searchDeliveries(Pageable pageable, DeliverySearchRequest request,
		String userId, String role, UUID hubManagerHubId);
}
