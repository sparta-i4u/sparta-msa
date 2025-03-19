package com.i4u.shipper.presentation.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.presentation.dtos.request.ShipperUserRequest;
import com.i4u.shipper.presentation.dtos.response.ShipperUserResponse;

import jakarta.ws.rs.Path;

@FeignClient(name = "user") // 혹은 auth
public interface UserClient {

	// Shipper -> User/Auth 로 해당 사용자가 존재하는지 + 권한이 무엇인지 확인
	@GetMapping("/users/{userId}")
	ShipperUserResponse confirmUser(@ModelAttribute ShipperUserRequest request /*userId, userRole or JWT 필요*/);

	@GetMapping("/api/v1/auth/validate-user/{userId}")
	ResponseEntity<CommonResponse<Map<String, Object>>> confirmUserByGateway(@PathVariable UUID userId);

}
