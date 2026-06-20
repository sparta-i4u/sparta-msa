package com.i4u.user.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class GatewayTokenFilter extends OncePerRequestFilter {

    private static final String GATEWAY_TOKEN_HEADER = "X-Gateway-Token";

    @Value("${gateway.internal-secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(GATEWAY_TOKEN_HEADER);

        if (!internalSecret.equals(token)) {
            log.warn("[GatewayTokenFilter] 직접 접근 차단 - uri: {}, remote: {}",
                    request.getRequestURI(), request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"status\":403,\"message\":\"Gateway를 통해서만 접근 가능합니다.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // actuator 헬스체크는 인프라 레벨에서 직접 접근하므로 제외
        return path.startsWith("/actuator");
    }
}
