//package com.i4u.user.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.i4u.user.application.UserService;
//import com.i4u.user.application.dtos.request.UserCreateRequestDto;
//import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
//import com.i4u.user.application.dtos.response.UserDetailResponseDto;
//import com.i4u.user.domain.UserRole;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserControllerTest {
//
//    @InjectMocks
//    private UserController userController;
//
//    @Mock
//    private UserService userService;
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//    private UUID userId;
//    private UserDetailResponseDto userResponse;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
//        objectMapper = new ObjectMapper();
//
//        userId = UUID.randomUUID();
//        userResponse = new UserDetailResponseDto(
//                userId, "testUser", "nickname", "test@email.com", "slack123",
//                "HUB_MANAGER", false, null, null
//        );
//    }
//
////    @Test
////    void 회원가입_성공() throws Exception {
////        // given
////        UserCreateRequestDto request = new UserCreateRequestDto(
////                "newUser", "password123", "newNickname", "new@email.com", "newSlack", UserRole.HUB_MANAGER // UserRole Enum 사용
////        );
////
////        when(userService.createUser(any(UserCreateRequestDto.class), any(String.class)))
////                .thenReturn(userResponse);
////
////        // when & then
////        mockMvc.perform(post("/api/v1/users/sign-up")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(request)))
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.username").value("testUser"));
////    }
//
//
//    @Test
//    void 사용자_조회_성공() throws Exception {
//        // given
//        when(userService.getUserById(userId)).thenReturn(Optional.of(userResponse));
//
//        // when & then
//        mockMvc.perform(get("/api/v1/users/{userId}", userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("testUser"));
//    }
//
//    @Test
//    void 사용자_조회_실패_존재하지_않음() throws Exception {
//        // given
//        when(userService.getUserById(userId)).thenReturn(Optional.empty());
//
//        // when & then
//        mockMvc.perform(get("/api/v1/users/{userId}", userId))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void 사용자_정보_수정_성공() throws Exception {
//        // given
//        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("newNickname", "newEmail@email.com", "HUB_MANAGER");
//
//        when(userService.updateUser(any(UUID.class), any(UUID.class), any(UserUpdateRequestDto.class)))
//                .thenReturn(userResponse);
//
//        // when & then
//        mockMvc.perform(put("/api/v1/users/{adminUserId}/{userId}", UUID.randomUUID(), userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nickname").value("nickname"));
//    }
//
//    @Test
//    void 사용자_삭제_성공() throws Exception {
//        // given
//        when(userService.deleteUser(any(UUID.class), any(UUID.class), any(UUID.class)))
//                .thenReturn(userResponse);
//
//        // when & then
//        mockMvc.perform(delete("/api/v1/users/{adminUserId}/{userId}", UUID.randomUUID(), userId)
//                        .param("deletedBy", UUID.randomUUID().toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("testUser"));
//    }
//}
