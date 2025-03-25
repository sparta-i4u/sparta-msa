package com.i4u.auth.presentation;

import com.i4u.auth.application.AuthService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestAuthControllerConfig {

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    // 다른 필요한 의존성도 여기서 추가 가능
}
