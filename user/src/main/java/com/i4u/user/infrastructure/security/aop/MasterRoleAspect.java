package com.i4u.user.infrastructure.security.aop;

import com.i4u.user.application.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class MasterRoleAspect {

    private static final String ROLE_HEADER = "X-User-Role";
    private static final String USER_ID_HEADER = "X-User-Id";

    @Around("@annotation(com.i4u.common.security.aop.RequiresMasterRole)")
    public Object checkMasterRole(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            log.error("[MasterRoleAspect] 요청 컨텍스트 없음 - 인증 불가");
            throw new UserException(UserException.UserErrorType.AUTHENTICATION_FAILED);
        }

        HttpServletRequest request = attrs.getRequest();
        String userRole = request.getHeader(ROLE_HEADER);
        String userId = request.getHeader(USER_ID_HEADER);

        if (userRole == null || userRole.isBlank()) {
            log.error("[MasterRoleAspect] {} 헤더 없음 - 인증 정보 없음", ROLE_HEADER);
            throw new UserException(UserException.UserErrorType.AUTHENTICATION_FAILED);
        }

        if (!"MASTER".equalsIgnoreCase(userRole)) {
            log.error("[MasterRoleAspect] MASTER 권한 없음 - userId: {}, role: {}", userId, userRole);
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        log.info("[MasterRoleAspect] MASTER 권한 확인 완료 - userId: {}", userId);
        return joinPoint.proceed();
    }
}
