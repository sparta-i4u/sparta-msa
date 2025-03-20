package com.i4u.user.infrastructure.security.aop;

import com.i4u.user.application.exception.UserException;
import com.i4u.user.domain.User;
import com.i4u.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MasterRoleAspect {

    private final UserRepository userRepository;

    @Around("@annotation(com.i4u.user.infrastructure.security.aop.RequiresMasterRole)")
    public Object checkMasterRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 현재 인증된 사용자의 ID 가져오기
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 사용자 정보 조회
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        // MASTER 권한 확인
        if (!user.getRole().isMaster()) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        log.info("MASTER 권한 확인 완료 - {}", user.getUsername());

        return joinPoint.proceed();
    }
}
