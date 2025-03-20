//package com.i4u.user.infrastructure.security.aop;
//
//import com.i4u.user.application.exception.UserException;
//import com.i4u.user.domain.User;
//import com.i4u.user.domain.UserRole;
//import com.i4u.user.domain.repository.UserRepository;
//import com.i4u.common.security.CustomUserDetails;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class MasterRoleAspectTest {
//
//    @InjectMocks
//    private MasterRoleAspect masterRoleAspect;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ProceedingJoinPoint joinPoint; // Mock 객체 생성
//
//    private User masterUser;
//    private User normalUser;
//    private UUID masterUserId;
//    private UUID normalUserId;
//
//    @BeforeEach
//    void setUp() {
//        masterUserId = UUID.randomUUID();
//        normalUserId = UUID.randomUUID();
//
//        masterUser = User.createUser("masterUser", "password123", "Master", "master@email.com", "slack123", UserRole.MASTER);
//        normalUser = User.createUser("normalUser", "password123", "User", "user@email.com", "slack123", UserRole.HUB_MANAGER);
//    }
//
//    @Test
//    void MASTER_권한이_있으면_정상_실행() throws Throwable {
//        // given
//        setSecurityContext(masterUserId, masterUser);
//        when(userRepository.findById(masterUserId)).thenReturn(Optional.of(masterUser));
//        when(joinPoint.proceed()).thenReturn("Success"); // proceed() 실행 가능하도록 설정
//
//        // when
//        Object result = masterRoleAspect.checkMasterRole(joinPoint);
//
//        // then
//        assertEquals("Success", result); // proceed()가 정상 실행됨
//        verify(joinPoint, times(1)).proceed(); // 정상 실행됨
//    }
//
//    @Test
//    void MASTER_권한이_없으면_예외발생() throws Throwable {
//        // given
//        setSecurityContext(normalUserId, normalUser);
//        when(userRepository.findById(normalUserId)).thenReturn(Optional.of(normalUser));
//
//        // proceed()가 실행되지 않도록 예외를 발생시키도록 설정
//        doThrow(new UserException(UserException.UserErrorType.PERMISSION_DENIED))
//                .when(joinPoint).proceed();
//
//        // when & then
//        assertThrows(UserException.class, () -> masterRoleAspect.checkMasterRole(joinPoint));
//
//        // proceed()가 실행되지 않아야 하므로 검증
//        verify(joinPoint, never()).proceed();
//    }
//
//
//    private void setSecurityContext(UUID userId, User user) {
//        CustomUserDetails userDetails = new com.i4u.user.infrastructure.security.UserDetailsImpl(user);
//        Authentication authentication = mock(Authentication.class);
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//
//        SecurityContextHolder.setContext(securityContext);
//    }
//}
