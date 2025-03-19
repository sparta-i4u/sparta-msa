package com.i4u.auth.infrastructure.client;

import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.exception.UserException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// FeignClient Fallback 구현체
// User 서비스가 비정상일 경우, 예외 처리를 수행
@Component
public class UserClientFallback implements UserClient {

    // ✅ User 서비스가 다운되었을 때 회원 가입 요청 실패 처리
    @Override
    public UserDetailResponseDto createUser(UserCreateRequestDto request, String encodedPassword) {
        throw new UserException(UserException.UserErrorType.INTERNAL_SERVER_ERROR);
    }

    // ✅ User 서비스가 다운되었을 때 사용자 정보 조회 실패 처리
    @Override
    public Map<String, Object> getUserInfo(UUID userId, List<String> fields) {
        return Collections.singletonMap("error", "User service is unavailable");
    }
}
