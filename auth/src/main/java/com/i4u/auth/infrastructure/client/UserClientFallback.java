package com.i4u.auth.infrastructure.client;

import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.exception.UserException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserDetailResponseDto createUser(UserCreateRequestDto request, String encodedPassword) {
        throw new UserException(UserException.UserErrorType.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Map<String, Object> getUserInfo(UUID userId, List<String> fields) {
        return Collections.singletonMap("error", "Auth service is unavailable");
    }
}