package com.i4u.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth 서비스 라우팅
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                // User 서비스 라우팅
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .uri("lb://USER-SERVICE"))

                // 기타 서비스 추가 가능
                .build();
    }
}
