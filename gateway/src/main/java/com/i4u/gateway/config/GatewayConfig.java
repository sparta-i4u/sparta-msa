package com.i4u.gateway.config;

import com.i4u.gateway.security.JwtAuthFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public GatewayConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // ✅ Auth 서비스 라우팅 (로그인/회원가입이므로 제한 완화)
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        //.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(new RedisRateLimiter(10, 20))))
                        .uri("lb://AUTH-SERVICE"))

                // ✅ User 서비스 라우팅
                .route("user-service", r -> r.path("/api/v1/users/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://USER-SERVICE"))

                // ✅ Common 서비스 라우팅
                .route("common-service", r -> r.path("/api/v1/common/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://COMMON-SERVICE"))

                // ✅ Delivery 서비스 라우팅
                .route("delivery-service", r -> r.path("/api/v1/deliveries/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://DELIVERY-SERVICE"))

                // ✅ Hub 서비스 라우팅
                .route("hub-service", r -> r.path("/api/v1/hubs/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://HUB-SERVICE"))

                // ✅ 추가된 서비스 라우팅 반영 (Message, Order, Product, Shipper 등)
                .route("message-service", r -> r.path("/api/v1/message/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://MESSAGE-SERVICE"))

                .route("order-service", r -> r.path("/api/v1/orders/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://ORDER-SERVICE"))

                .route("product-service", r -> r.path("/api/v1/products/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://PRODUCT-SERVICE"))

                .route("shipper-service", r -> r.path("/api/v1/shippers/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://SHIPPER-SERVICE"))

                .route("company-service", r -> r.path("/api/v1/companies/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://COMPANY-SERVICE"))

                .route("hub-connection-service", r -> r.path("/api/v1/hub-connections/**")
                        .filters(f -> applyFilters(f))
                        .uri("lb://HUB-CONNECTION-SERVICE"))

                // ✅ Fallback 처리 라우트
                .route("fallback-route", r -> r.path("/fallback")
                        .uri("no://op"))

                .build();
    }

    // ✅ 공통 필터 적용 메서드 (JWT 인증 필터 적용)
    private GatewayFilterSpec applyFilters(GatewayFilterSpec f) {
        return f.filter(convertToGatewayFilter(jwtAuthFilter));
        //.requestRateLimiter(c -> c.setRateLimiter(new RedisRateLimiter(replenishRate, burstCapacity)));
    }

    // ✅ `GlobalFilter`를 `GatewayFilter`로 변환하는 메서드
    private GatewayFilter convertToGatewayFilter(GlobalFilter filter) {
        return (exchange, chain) -> filter.filter(exchange, chain);
    }
}