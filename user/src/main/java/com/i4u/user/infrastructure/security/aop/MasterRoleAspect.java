package com.i4u.user.infrastructure.security.aop;

import com.i4u.user.application.exception.UserException;
import com.i4u.user.domain.User;
import com.i4u.user.domain.repository.UserRepository;
import com.i4u.common.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

// MASTER 권한이 필요한 메서드에 대한 AOP 기반 권한 검사
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MasterRoleAspect {

    private final UserRepository userRepository;

    @Around("@annotation(com.i4u.user.infrastructure.security.aop.RequiresMasterRole)")
    public Object checkMasterRole(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        UUID userId = userDetails.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        if (!user.getRole().isMaster()) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        log.info("MASTER 권한 확인 완료 - {}", user.getUsername());

        return joinPoint.proceed(); // MASTER 권한이 있으면 메서드 실행
    }
}
