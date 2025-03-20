package com.i4u.auth.infrastructure.security.aop;

import com.i4u.common.security.CustomUserDetails;
import com.i4u.auth.application.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthAspect {

    @Around("@annotation(com.i4u.user.infrastructure.security.aop.RequiresAuth)")
    public Object checkAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("인증 정보가 존재하지 않습니다. 로그인 필요");
            throw new AuthException(AuthException.AuthErrorType.AUTHENTICATION_FAILED);
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            log.error("SecurityContext에서 사용자 정보를 찾을 수 없습니다.");
            throw new AuthException(AuthException.AuthErrorType.PERMISSION_DENIED);
        }

        UUID userId = userDetails.getUserId();
        log.info("✅ [AuthAspect] 로그인 검증 완료 - userId: {}", userId);

        return joinPoint.proceed(); // 로그인 검증이 통과되면 메서드 실행
    }
}
