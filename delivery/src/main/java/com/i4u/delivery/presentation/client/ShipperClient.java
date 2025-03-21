package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.presentation.dtos.request.DeliveryShipperRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;

@FeignClient(name = "SHIPPER-SERVICE")
public interface ShipperClient {

	@GetMapping("/api/v1/shippers/deliveries")
	ResponseEntity<CommonResponse<DeliveryShipperResponse>> assignShipper(@ModelAttribute DeliveryShipperRequest request);

}