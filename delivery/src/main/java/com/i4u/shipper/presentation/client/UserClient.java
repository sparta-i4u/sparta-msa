package com.i4u.shipper.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.i4u.shipper.presentation.dtos.request.ShipperUserRequest;
import com.i4u.shipper.presentation.dtos.response.ShipperUserResponse;

@FeignClient(name = "user") // 혹은 auth
public interface UserClient {

	// Shipper -> User/Auth 로 해당 사용자가 존재하는지 + 권한이 무엇인지 확인
	@GetMapping("/users/{userId}")
	ShipperUserResponse confirmUser(@ModelAttribute ShipperUserRequest request /*userId, userRole or JWT 필요*/);

}
