package com.i4u.auth.infrastructure.security.aop;

import com.i4u.common.security.CustomUserDetails;
import com.i4u.user.application.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;
// 로그인 검증(AOP 기반)을 수행
//`@RequiresAuth` 어노테이션이 적용된 메서드에 대한 로그인 검증 AOP.
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthAspect {

    @Around("@annotation(com.i4u.user.infrastructure.security.aop.RequiresAuth)")
    public Object checkAuthentication(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        UUID userId = userDetails.getUserId();
        log.info("✅ 로그인 검증 완료 - userId: {}", userId);

        return joinPoint.proceed(); // 로그인 검증이 통과되면 메서드 실행
    }
}
