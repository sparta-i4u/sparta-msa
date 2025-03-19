package com.i4u.auth.infrastructure.client;

import com.i4u.common.config.FeignConfig;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// Auth 서비스에서 User 서비스 API를 호출할 때 사용
// FeignConfig.class를 적용하여 GlobalFeignExceptionHandler가 자동으로 동작하도록 설정
@FeignClient(name = "user-service", path = "/api/v1/users", configuration = FeignConfig.class, fallback = UserClientFallback.class, primary = false)
public interface UserClient {

    // Auth 서비스에서 User 서비스로 회원 가입 요청을 보냄
    // return 생성된 사용자 정보
    @PostMapping
    UserDetailResponseDto createUser(
            @RequestBody UserCreateRequestDto request,
            @RequestParam String encodedPassword
    );

    // 특정 필드 기준으로 사용자 정보를 조회하는 Feign Client 메서드
    // Auth 서비스에서 사용자가 존재하는지 검증할 때 활용
    // @return 사용자 정보가 포함된 맵
    @GetMapping("/validate-user/{userId}")
    Map<String, Object> getUserInfo(
            @PathVariable UUID userId,
            @RequestParam(required = false) List<String> fields
    );
}
