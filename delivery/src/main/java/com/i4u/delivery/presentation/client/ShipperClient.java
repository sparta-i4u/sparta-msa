package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.i4u.delivery.presentation.dtos.request.DeliveryShipperCreateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryShipperUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperUpdateResponse;

import lombok.Getter;

@FeignClient(name = "shipper")
public interface ShipperClient {

	@GetMapping("/deliveries/{deliveryId}/shippers/")
	DeliveryShipperCreateResponse assignShipper(DeliveryShipperCreateRequest build);

	@GetMapping("/deliveries/{deliveryId}/shippers/new-info")
	DeliveryShipperUpdateResponse updateShipperInfo(DeliveryShipperUpdateRequest build);
}
