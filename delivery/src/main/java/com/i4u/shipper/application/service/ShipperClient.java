package com.i4u.shipper.application.service;

import java.util.UUID;

import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;

public interface ShipperClient {

	public DeliveryShipperResponse assignShipper(UUID recipientHubId);

}