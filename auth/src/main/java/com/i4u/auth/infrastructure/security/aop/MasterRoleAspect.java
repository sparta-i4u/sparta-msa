package com.i4u.auth.infrastructure.security.aop;

import com.i4u.auth.infrastructure.client.UserClient;
import com.i4u.user.application.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

// MASTER 권한이 필요한 메서드에 대한 AOP 기반 권한 검사
@Aspect // AOP(Aspect-Oriented Programming) 기능을 활성화
@Component // Spring Bean으로 등록하여 사용 가능
@RequiredArgsConstructor // 생성자 주입을 자동으로 생성
@Slf4j // 로그 기록을 위한 Lombok 어노테이션
public class MasterRoleAspect {

    private final UserClient userClient; // UserClient를 통해 사용자 정보를 조회

    @Around("@annotation(com.i4u.user.infrastructure.security.aop.RequiresMasterRole)")
    public Object checkMasterRole(ProceedingJoinPoint joinPoint) throws Throwable {

        // 현재 인증된 사용자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 정보가 없거나, 사용자 정보가 CustomUserDetails 타입이 아닐 경우 예외 발생
        if (authentication == null || !(authentication.getPrincipal() instanceof com.i4u.common.security.CustomUserDetails userDetails)) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        UUID userId = userDetails.getUserId(); // 현재 로그인한 사용자의 ID 가져오기

        // UserClient를 이용해 사용자의 역할(Role) 정보를 가져옴
        Map<String, Object> userInfo = userClient.getUserInfo(userId, java.util.List.of("userId", "role"));

        // 사용자 정보가 없거나, MASTER 권한이 없으면 예외 발생
        if (userInfo == null || !"MASTER".equals(userInfo.get("role"))) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        log.info("MASTER 권한 확인 완료 - UserID: {}", userId);

        return joinPoint.proceed(); // MASTER 권한이 있으면 메서드 실행
    }
}
