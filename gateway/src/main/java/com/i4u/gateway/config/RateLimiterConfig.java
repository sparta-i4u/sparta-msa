package com.i4u.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    // ✅ IP 기반 Rate Limiting 적용
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }
}
// Gateway에서 동일한 IP에 대해 요청 속도 제한 가능
//KeyResolver를 사용하여 클라이언트의 IP 주소 기반 요청 제한