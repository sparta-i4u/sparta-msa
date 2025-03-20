//package com.i4u.user.application;
//
//import com.i4u.user.application.dtos.request.UserCreateRequestDto;
//import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
//import com.i4u.user.application.dtos.response.UserDetailResponseDto;
//import com.i4u.user.application.exception.UserException;
//import com.i4u.user.domain.User;
//import com.i4u.user.domain.UserRole;
//import com.i4u.user.domain.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceTest {
//
//    @InjectMocks
//    private UserService userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//
//    private UUID userId;
//    private User testUser;
//
//    @BeforeEach
//    void setUp() {
//        userId = UUID.randomUUID();
//        testUser = User.createUser(
//                "testUser", "password123", "nickname", "test@email.com", "slack123", UserRole.HUB_MANAGER
//        );
//    }
//
//    @Test
//    void 회원가입_성공() {
//        // given
//        UserCreateRequestDto request = new UserCreateRequestDto(
//                "newUser", "password123", "newNickname", "new@email.com", "newSlack", "HUB_MANAGER"
//        );
//
//        String encodedPassword = "encodedPassword123";
//        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);
//        when(userRepository.findBySlackIdAndIsDeletedFalse(request.getSlackId())).thenReturn(Optional.empty());
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
//            User user = invocation.getArgument(0);
//            return User.createUser(user.getUsername(), user.getPassword(), user.getNickname(), user.getEmail(), user.getSlackId(), user.getRole());
//        });
//
//        // when
//        UserDetailResponseDto response = userService.createUser(request, encodedPassword); // ✅ Auth에서 암호화된 비밀번호 전달
//
//        // then
//        assertNotNull(response);
//        assertEquals(request.getUsername(), response.getUsername());
//        assertEquals("HUB_MANAGER", response.getRole());
//    }
//
//    @Test
//    void 회원가입_실패_중복된_SlackID() {
//        // given
//        UserCreateRequestDto request = new UserCreateRequestDto(
//                "duplicateUser", "password123", "nickname", "email@email.com", "slack123", "HUB_MANAGER"
//        );
//
//        when(userRepository.findBySlackIdAndIsDeletedFalse(request.getSlackId()))
//                .thenReturn(Optional.of(testUser));
//
//        // when & then
//        assertThrows(UserException.class, () -> userService.createUser(request, "encodedPassword123"));
//    }
//
//    @Test
//    void 사용자_조회_성공() {
//        // given
//        when(userRepository.findByUserIdAndIsDeletedFalse(userId))
//                .thenReturn(Optional.of(testUser));
//
//        // when
//        Optional<UserDetailResponseDto> response = userService.getUserById(userId);
//
//        // then
//        assertTrue(response.isPresent());
//        assertEquals(testUser.getUsername(), response.get().getUsername());
//    }
//
//    @Test
//    void 사용자_조회_실패_존재하지_않음() {
//        // given
//        when(userRepository.findByUserIdAndIsDeletedFalse(userId))
//                .thenReturn(Optional.empty());
//
//        // when & then
//        assertThrows(UserException.class, () -> userService.getUserById(userId));
//    }
//
//    @Test
//    void 사용자_정보_수정_성공() {
//        // given
//        UUID adminId = UUID.randomUUID(); // 관리자 UUID
//        UUID userId = UUID.randomUUID();  // 수정할 사용자 UUID
//
//        User adminUser = User.createUser("admin", "admin123", "Admin", "admin@email.com", "adminSlack", UserRole.MASTER);
//        User targetUser = User.createUser("targetUser", "password123", "OldNickname", "old@email.com", "slack456", UserRole.HUB_MANAGER);
//
//        // 관리자 계정이 존재하는 경우
//        when(userRepository.findByUserIdAndIsDeletedFalse(adminId)).thenReturn(Optional.of(adminUser));
//        // 수정 대상 사용자 계정이 존재하는 경우
//        when(userRepository.findByUserIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(targetUser));
//
//        // 변경 요청 데이터 (닉네임 & 이메일 수정)
//        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("newNickname", "newEmail@email.com", "HUB_MANAGER");
//
//        // when
//        UserDetailResponseDto response = userService.updateUser(adminId, userId, updateRequest);
//
//        // then
//        assertNotNull(response);
//        assertEquals("newNickname", response.getNickname()); // 닉네임 변경 확인
//        assertEquals("newEmail@email.com", response.getEmail()); // 이메일 변경 확인
//    }
//
//
//
//    @Test
//    void 사용자_삭제_성공() {
//        // given
//        UUID adminId = UUID.randomUUID();
//        User adminUser = User.createUser("admin", "admin123", "Admin", "admin@email.com", "adminSlack", UserRole.MASTER);
//        when(userRepository.findByUserIdAndIsDeletedFalse(adminId)).thenReturn(Optional.of(adminUser));
//        when(userRepository.findByUserIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(testUser));
//
//        // when
//        UserDetailResponseDto response = userService.deleteUser(adminId, userId, adminId);
//
//        // then
//        assertTrue(response.isDeleted());
//    }
//
//    @Test
//    void 사용자_삭제_실패_MASTER_권한_없음() {
//        // given
//        UUID nonMasterAdminId = UUID.randomUUID();
//        User nonMasterUser = User.createUser("user", "user123", "User", "user@email.com", "userSlack", UserRole.HUB_MANAGER);
//        when(userRepository.findByUserIdAndIsDeletedFalse(nonMasterAdminId)).thenReturn(Optional.of(nonMasterUser));
//
//        // when & then
//        assertThrows(UserException.class, () -> userService.deleteUser(nonMasterAdminId, userId, nonMasterAdminId));
//    }
//}
