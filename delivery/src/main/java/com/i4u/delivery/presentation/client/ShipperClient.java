package com.i4u.delivery.presentation.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.presentation.dtos.request.DeliveryShipperRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;

// public interface ShipperClient {
//
// 	@GetMapping("/api/v1/shippers/deliveries/{recipientHubId}")
// 	ResponseEntity<CommonResponse<DeliveryShipperResponse>> assignShipper(@PathVariable UUID recipientHubId);
//
// }