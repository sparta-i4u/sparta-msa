package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.i4u.delivery.presentation.dtos.request.DeliveryUserSlackIdRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryUserUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryUserSlackIdResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryUserUpdateResponse;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/deliveries/{deliveryId}/users/confirm")
	DeliveryUserSlackIdResponse confirmUser(DeliveryUserSlackIdRequest build);

	@GetMapping("/deliveries/{deliveryId}/users/new-info")
	DeliveryUserUpdateResponse updateUserInfo(DeliveryUserUpdateRequest build);
}