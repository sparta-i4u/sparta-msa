package com.i4u.message.infrastructure.client;

import com.i4u.message.infrastructure.dto.ConfirmUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    // Shipper/Delivery -> User/Auth 로 해당 사용자가 존재하는지 + 권한이 무엇인지 확인
    @GetMapping("/api/v1/auth/user-info/{userId}")
    ConfirmUserResponse confirmUser(@PathVariable UUID userId);
}