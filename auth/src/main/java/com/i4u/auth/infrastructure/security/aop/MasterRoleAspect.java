package com.i4u.auth.infrastructure.security.aop;

import com.i4u.auth.infrastructure.client.UserClient;
import com.i4u.auth.application.exception.AuthException;
import com.i4u.auth.application.dtos.response.AuthUserInfoResponseDto;
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
public class MasterRoleAspect {

    private final UserClient userClient;

    @Around("@annotation(com.i4u.user.infrastructure.security.aop.RequiresMasterRole)")
    public Object checkMasterRole(ProceedingJoinPoint joinPoint) throws Throwable {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("인증 정보가 존재하지 않음 - MASTER 권한 검증 실패");
            throw new AuthException(AuthException.AuthErrorType.PERMISSION_DENIED);
        }

        if (!(authentication.getPrincipal() instanceof com.i4u.common.security.CustomUserDetails userDetails)) {
            log.error("SecurityContext에서 사용자 정보를 찾을 수 없음");
            throw new AuthException(AuthException.AuthErrorType.PERMISSION_DENIED);
        }

        UUID userId = userDetails.getUserId();

        AuthUserInfoResponseDto userInfo = userClient.getUserInfo(userId);

        if (userInfo == null || !"MASTER".equals(userInfo.getRole().name())) {
            log.error("MASTER 권한이 없는 사용자 - userId: {}", userId);
            throw new AuthException(AuthException.AuthErrorType.PERMISSION_DENIED);
        }

        log.info("MASTER 권한 확인 완료 - userId: {}", userId);

        return joinPoint.proceed();
    }
}
