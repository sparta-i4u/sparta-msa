package com.i4u.gateway.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalLoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        log.info("➡️ 요청: {} {} from {}", request.getMethod(), request.getURI(), request.getRemoteAddress());
        return chain.filter(exchange).doOnSuccess(aVoid ->
                log.info("⬅️ 응답: {} {} - Status: {}", request.getMethod(), request.getURI(), response.getStatusCode()));
    }
}
//Spring Cloud Gateway에서 모든 요청과 응답이 자동으로 로깅될 수 있어.
//요청 로깅: HTTP 메서드, URI, 클라이언트 IP 주소
//응답 로깅: HTTP 상태 코드 및 요청 정보
