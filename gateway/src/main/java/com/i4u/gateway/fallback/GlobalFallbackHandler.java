package com.i4u.gateway.fallback;

import com.i4u.common.utils.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class GlobalFallbackHandler {

    public static Mono<ServerResponse> handleFallback(ServerWebExchange exchange) {
        Throwable throwable = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        String errorMessage = (throwable != null) ? throwable.getMessage() : "서비스를 사용할 수 없습니다. 나중에 다시 시도해주세요.";

        // 장애 발생 시 로깅 추가
        log.error("⛔ Gateway Fallback 발생: {}", errorMessage);

        CommonResponse<String> response = CommonResponse.fail("503", errorMessage);

        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(response);
    }
}
