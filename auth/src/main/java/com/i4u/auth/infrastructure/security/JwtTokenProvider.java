package com.i4u.auth.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key secretKey;
    private static final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 7일

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ 액세스 토큰 생성 (`userId` 포함)
    public String createAccessToken(UUID userId, String email, String role) {
        return createToken(userId.toString(), email, role, ACCESS_TOKEN_VALIDITY);
    }

    // ✅ 리프레시 토큰 생성 (`userId` 포함)
    public String createRefreshToken(UUID userId, String email) {
        return createToken(userId.toString(), email, "REFRESH", REFRESH_TOKEN_VALIDITY);
    }

    // ✅ 공통 토큰 생성 메서드 (`userId` 포함)
    private String createToken(String userId, String email, String role, long validity) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId) // `userId` 추가
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 토큰 검증 (`parser()` 유지)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ 토큰에서 `email` 추출
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // ✅ 토큰에서 `userId` 추출
    public UUID getUserIdFromToken(String token) {
        String userId = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
        return UUID.fromString(userId);
    }
}
