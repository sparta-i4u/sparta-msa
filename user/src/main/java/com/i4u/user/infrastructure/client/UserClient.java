package com.i4u.user.infrastructure.client;

import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Auth 서비스에서 User 서비스로 회원가입 요청을 보낼 때 사용하는 FeignClient
@FeignClient(name = "user-service", path = "/api/v1/users", fallback = com.i4u.user.infrastructure.client.UserClientFallback.class)
public interface UserClient {

    @PostMapping
    UserDetailResponseDto createUser(@RequestBody UserCreateRequestDto request);
}
