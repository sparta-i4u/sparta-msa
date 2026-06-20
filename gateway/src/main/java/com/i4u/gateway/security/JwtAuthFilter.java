package com.i4u.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${gateway.internal-secret}")
    private String internalSecret;

    private final List<String> excludedRoutes = List.of("/api/v1/auth/sign-in", "/api/v1/auth/sign-up");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        Predicate<ServerHttpRequest> isSecured = r ->
                excludedRoutes.stream().noneMatch(uri -> r.getURI().getPath().contains(uri));

        if (isSecured.test(request)) {
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(response, "Authorization 헤더가 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                return onError(response, "Bearer 토큰이 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            token = token.substring(7);

            try {
                // JWT 파싱 및 검증
                JwtParser jwtParser = Jwts.parser().verifyWith((javax.crypto.SecretKey) getSigningKey()).build();
                Claims claims = jwtParser.parseSignedClaims(token).getPayload();

                String userId = claims.get("userId", String.class);
                String userEmail = claims.getSubject();
                String userRole = claims.get("role", String.class);

                if (Objects.isNull(userId) || Objects.isNull(userEmail) || Objects.isNull(userRole)) {
                    return onError(response, "JWT Claims가 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
                }

                log.info("인증된 사용자: ID={}, Email={}, 역할={}", userId, userEmail, userRole);

                ServerHttpRequest modifiedRequest = request.mutate()
                        .headers(httpHeaders -> {
                            httpHeaders.add("X-User-Id", userId);
                            httpHeaders.add("X-User-Email", userEmail);
                            httpHeaders.add("X-User-Role", userRole);
                            httpHeaders.add("X-Gateway-Token", internalSecret);
                        })
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build())
                        .contextWrite(context -> context.put("userId", userId).put("userEmail", userEmail).put("userRole", userRole));
            } catch (ExpiredJwtException e) {
                log.error("JWT 토큰 만료: {}", e.getMessage());
                return onError(response, "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
            } catch (JwtException e) {
                log.error("JWT 검증 실패: {}", e.getMessage());
                return onError(response, "JWT 검증 실패: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        }
        // 인증 제외 경로도 X-Gateway-Token 추가 (Auth sign-in/up 등 내부 서비스 전달)
        ServerHttpRequest gatewayTagged = exchange.getRequest().mutate()
                .headers(h -> h.add("X-Gateway-Token", internalSecret))
                .build();
        return chain.filter(exchange.mutate().request(gatewayTagged).build());
    }

    // 인증 오류 응답을 JSON 형식으로 반환하는 메서드
    private Mono<Void> onError(ServerHttpResponse response, String message, HttpStatus status) {
        log.error("JWT 인증 오류: {}", message);
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorResponse = "{\"status\": \"" + status.value() + "\", \"message\": \"" + message + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorResponse.getBytes())));
    }

    // JWT 서명 키 반환
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
