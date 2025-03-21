package com.i4u.auth.infrastructure.client;

import com.i4u.auth.application.dtos.response.AuthUserInfoResponseDto;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDetailResponseDto createUser(UserCreateRequestDto request) { // ✅ `encodedPassword` 제거
        log.error("User 서비스 장애로 회원가입 요청 실패");
        log.error("[UserClientFallback] User 서비스 접근 불가 - 회원 생성 실패. 요청 데이터: {}", request);
        throw new UserException(UserException.UserErrorType.INTERNAL_SERVER_ERROR);
    }

    @Override
    public AuthUserInfoResponseDto getUserInfo(UUID userId) {
        log.error("User 서비스 장애로 사용자 정보 조회 실패 - userId: {}", userId);
        throw new UserException(UserException.UserErrorType.USER_NOT_FOUND);
    }
}