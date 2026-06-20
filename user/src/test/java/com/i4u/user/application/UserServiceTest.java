//package com.i4u.user.application;
//
//import com.i4u.user.application.dtos.request.UserCreateRequestDto;
//import com.i4u.user.application.dtos.request.UserSearchRequestDto;
//import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
//import com.i4u.user.application.dtos.response.UserDetailResponseDto;
//import com.i4u.user.application.dtos.response.UserListResponseDto;
//import com.i4u.user.domain.User;
//import com.i4u.common.domain.UserRole;
//import com.i4u.user.domain.repository.UserRepository;
//import com.i4u.user.application.exception.UserException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.data.domain.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.lang.reflect.Field;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static com.i4u.user.domain.UserRole.MASTER;
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("회원가입 - 정상 생성")
//    void createUser() throws Exception {
//        // given
//        UserCreateRequestDto dto = UserCreateRequestDto.builder()
//                .username("newuser")
//                .password("password123")
//                .nickname("Newbie")
//                .email("new@example.com")
//                .slackId("slack-new")
//                .role(UserRole.MASTER)
//                .build();
//
//        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
//
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
//            User userToSave = invocation.getArgument(0);
//
//            User user = User.builder()
//                    .userId(UUID.randomUUID())
//                    .username(userToSave.getUsername())
//                    .password(userToSave.getPassword())
//                    .nickname(userToSave.getNickname())
//                    .email(userToSave.getEmail())
//                    .slackId(userToSave.getSlackId())
//                    .role(userToSave.getRole())
//                    .build();
//
//            user.updateDelete(false);
//
//            // createdAt, updatedAt 강제 설정 (리플렉션 사용)
//            Field createdAtField = User.class.getSuperclass().getDeclaredField("createdAt");
//            createdAtField.setAccessible(true);
//            createdAtField.set(user, LocalDateTime.now());
//
//            Field updatedAtField = User.class.getSuperclass().getDeclaredField("updatedAt");
//            updatedAtField.setAccessible(true);
//            updatedAtField.set(user, LocalDateTime.now());
//
//            return user;
//        });
//
//        // when
//        var result = userService.createUser(dto, "password123");
//
//        // then
//        assertNotNull(result);
//        assertEquals("new@example.com", result.getEmail());
//        assertEquals("Newbie", result.getNickname());
//        assertNotNull(result.getUserId());
//    }
//
//
//    @Test
//    void getUserById() {
//        UUID userId = UUID.randomUUID();
//        User user = mock(User.class);
//        when(userRepository.findByUserIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(user));
//
//        UserDetailResponseDto result = userService.getUserById(userId);
//
//        assertThat(result).isNotNull();
//    }
//
//    @Test
//    void getUserBySlackId() {
//        String slackId = "slack123";
//        User user = mock(User.class);
//        when(userRepository.findBySlackIdAndIsDeletedFalse(slackId)).thenReturn(Optional.of(user));
//
//        UserDetailResponseDto result = userService.getUserBySlackId(slackId);
//        assertThat(result).isNotNull();
//    }
//
//    @Test
//    void getAllUsers() {
//        Page<User> userPage = new PageImpl<>(List.of(mock(User.class)));
//        when(userRepository.findAllByIsDeletedFalse(any())).thenReturn(userPage);
//
//        UserListResponseDto response = userService.getAllUsers(PageRequest.of(0, 10));
//        assertThat(response).isNotNull();
//    }
//
//    @Test
//    void deleteUser() {
//        UUID adminId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID deletedBy = UUID.randomUUID();
//
//        User mockUser = mock(User.class);
//        when(userRepository.findByUserIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(mockUser));
//
//        UserDetailResponseDto result = userService.deleteUser(adminId, userId, deletedBy);
//        assertThat(result).isNotNull();
//        verify(userRepository).save(mockUser);
//    }
//
//    @Test
//    void updateUser() {
//        // given
//        UUID adminUserId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//
//        UserUpdateRequestDto requestDto = new UserUpdateRequestDto("newNickname", "new@example.com", "HUB");
//
//        User mockAdmin = mock(User.class);
//        User mockUser = mock(User.class);
//
//        when(userRepository.findByUserIdAndIsDeletedFalse(adminUserId)).thenReturn(Optional.of(mockAdmin));
//        when(mockAdmin.getRole()).thenReturn(MASTER); // isAdmin = true
//
//        when(userRepository.findByUserIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(mockUser));
//        when(mockUser.getNickname()).thenReturn("oldNickname");
//        when(mockUser.getEmail()).thenReturn("old@example.com");
//        when(mockUser.getRole()).thenReturn(UserRole.MASTER); // 기존 role이 필요할 경우
//
//        // when
//        UserDetailResponseDto result = userService.updateUser(adminUserId, userId, requestDto);
//
//        // then
//        verify(mockUser).updateUser("newNickname", "new@example.com", adminUserId, true);
//        verify(userRepository).save(mockUser);
//        assertThat(result).isNotNull();
//    }
//
//
//    void updateUserRole() {
//        // given
//        UUID adminUserId = UUID.randomUUID(); // 추가된 부분
//        UUID targetUserId = UUID.randomUUID();
//        String newRole = "MASTER";
//        User mockUser = mock(User.class);
//        when(userRepository.findByUserIdAndIsDeletedFalse(targetUserId)).thenReturn(Optional.of(mockUser));
//
//        // when
//        UserDetailResponseDto result = userService.updateUserRole(adminUserId, targetUserId, newRole);
//
//        // then
//        verify(mockUser).updateRole(UserRole.MASTER, UserRole.MASTER); // 예상되는 내부 동작에 맞게 수정
//        verify(userRepository).save(mockUser);
//        assertThat(result).isNotNull();
//    }
//
//    @Test
//    void searchUsers() {
//        // given
//        String keyword = "test";
//        Pageable pageable = PageRequest.of(0, 10);
//        UserSearchRequestDto searchRequestDto = new UserSearchRequestDto(keyword, null, pageable);
//        UUID requestUserId = UUID.randomUUID();
//        UserRole requestUserRole = UserRole.MASTER;
//
//        Page<User> userPage = new PageImpl<>(List.of(mock(User.class)));
//
//        when(userRepository.searchUsers(keyword, null, pageable, false)).thenReturn(userPage);
//
//        // when
//        UserListResponseDto result = userService.searchUsers(searchRequestDto, requestUserId, requestUserRole);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getUsers()).isNotEmpty();
//    }
//
//}
