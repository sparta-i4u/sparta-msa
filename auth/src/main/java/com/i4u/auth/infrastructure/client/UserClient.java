package com.i4u.auth.infrastructure.client;

import com.i4u.auth.application.dtos.response.AuthUserInfoResponseDto;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "user-service", path = "/api/v1/users", fallback = UserClientFallback.class)
public interface UserClient {

    @PostMapping
    UserDetailResponseDto createUser(@RequestBody UserCreateRequestDto request); // ✅ encodedPassword 제거

    @GetMapping("/auth-user-info/{userId}")
    AuthUserInfoResponseDto getUserInfo(@PathVariable UUID userId);
}
