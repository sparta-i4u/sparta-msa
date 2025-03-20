package com.i4u.gateway.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("[Gateway 요청] {} {}", request.getMethod(), request.getURI());
        log.info("[요청 헤더] {}", request.getHeaders());

        return chain.filter(exchange).doAfterTerminate(() -> {
            ServerHttpResponse response = exchange.getResponse();
            log.info("✅ [Gateway 응답] 상태 코드: {}", response.getStatusCode());
        });
    }

    @Override
    public int getOrder() {
        return -1; // 필터를 가장 먼저 실행하도록 설정
    }
}
