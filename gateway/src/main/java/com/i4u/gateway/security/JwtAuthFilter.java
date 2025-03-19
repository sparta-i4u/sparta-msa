package com.i4u.gateway.security;

import io.jsonwebtoken.Claims;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private final List<String> excludedRoutes = List.of("/api/v1/auth/sign-in", "/api/v1/auth/sign-up");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 예외 경로 (로그인, 회원가입 등)에서는 JWT 검증 제외
        Predicate<ServerHttpRequest> isSecured = r ->
                excludedRoutes.stream().noneMatch(uri -> r.getURI().getPath().contains(uri));

        if (isSecured.test(request)) {
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (token == null || !token.startsWith("Bearer ")) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            token = token.substring(7);

            try {
                // JWT 파싱
                JwtParser jwtParser = Jwts.parser()
                        .setSigningKey(getSigningKey())
                        .build();

                Claims claims = jwtParser.parseSignedClaims(token).getPayload();

                // userId, userEmail, userRole 추출
                String userId = claims.get("userId", String.class);
                String userEmail = claims.getSubject();
                String userRole = claims.get("role", String.class);

                log.info("✅ 인증된 사용자: ID={}, Email={}, 역할={}", userId, userEmail, userRole);

                // Gateway에서 `X-User-Id` 포함하여 User & Auth 서비스로 전달
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", userId) // `userId` 추가
                        .header("X-User-Email", userEmail)
                        .header("X-User-Role", userRole)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("⛔ JWT 검증 실패: {}", e.getMessage());
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }

        return chain.filter(exchange);
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
