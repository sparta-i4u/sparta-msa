package com.i4u.user.infrastructure.client;

import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.exception.UserException;
import org.springframework.stereotype.Component;

// UserClient가 실패할 경우 Fallback 메서드를 실행
@Component
public class UserClientFallback implements com.i4u.user.infrastructure.client.UserClient {

    @Override
    public UserDetailResponseDto createUser(UserCreateRequestDto request) {
        throw new UserException(UserException.UserErrorType.INTERNAL_SERVER_ERROR);
    }
}
