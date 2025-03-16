//package com.i4u.user.infrastructure.security.aop;
//
//import com.i4u.user.application.exception.UserException;
//import com.i4u.user.domain.UserRole;
//import com.i4u.user.infrastructure.security.util.SecurityUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Slf4j
//@Aspect
//@Component
//public class AuthorizationAspect {
//
//    // 권한 검사가 필요한 메서드를 정의
//    @Pointcut("@annotation(com.i4u.user.infrastructure.security.aop.RequiresMasterRole)")
//    public void masterRoleRequired() {}
//
//    // MASTER 권한이 있는지 확인하는 AOP 메서드
//    @Before("masterRoleRequired()")
//    public void checkMasterRole() {
//        UUID currentUserId = SecurityUtil.getCurrentUserId()
//                .orElseThrow(() -> new UserException(UserException.UserErrorType.PERMISSION_DENIED));
//
//        UserRole currentUserRole = SecurityUtil.getCurrentUserRole()
//                .orElseThrow(() -> new UserException(UserException.UserErrorType.PERMISSION_DENIED));
//
//        if (!currentUserRole.isMaster()) {
//            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
//        }
//
//        log.info(" MASTER 권한 확인 완료 - userId: {}", currentUserId);
//    }
//}
