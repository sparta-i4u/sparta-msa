package com.i4u.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.presentation.dtos.request.DeliveryUserSlackIdRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryUserUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryUserSlackIdResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryUserUpdateResponse;
import com.i4u.shipper.presentation.dtos.request.ShipperUserRequest;
import com.i4u.shipper.presentation.dtos.response.ShipperUserResponse;

@FeignClient(name = "user-service")
public interface UserClient {

	// Shipper -> User/Auth 로 해당 사용자가 존재하는지 + 권한이 무엇인지 확인
	@GetMapping("/users/{userId}")
	ShipperUserResponse confirmUser(@ModelAttribute ShipperUserRequest request /*userId, userRole or JWT 필요*/);

	@GetMapping("/api/v1/auth/validate-user/{userId}")
	ResponseEntity<CommonResponse<Map<String, Object>>> confirmUserByGateway(@PathVariable UUID userId);

	@GetMapping("/deliveries/{deliveryId}/users/confirm")
	DeliveryUserSlackIdResponse confirmUser(DeliveryUserSlackIdRequest build);

	@GetMapping("/deliveries/{deliveryId}/users/new-info")
	DeliveryUserUpdateResponse updateUserInfo(DeliveryUserUpdateRequest build);

}
