package com.i4u.auth.infrastructure.client;

import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "auth-service", path = "/api/v1/auth", fallback = UserClientFallback.class)
public interface UserClient {

    // ✅ Auth 서비스에서 User 생성 요청
    @PostMapping("/users")
    UserDetailResponseDto createUser(
            @RequestBody UserCreateRequestDto request,
            @RequestParam String encodedPassword
    );

    // ✅ 특정 필드 기준으로 사용자 정보를 조회하는 Feign Client 메서드 (Auth 서비스에서 제공)
    @GetMapping("/validate-user/{userId}")
    Map<String, Object> getUserInfo(
            @PathVariable UUID userId,
            @RequestParam(required = false) List<String> fields
    );
}
