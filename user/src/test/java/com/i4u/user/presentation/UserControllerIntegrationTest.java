//package com.i4u.user.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.i4u.user.application.UserService;
//import com.i4u.user.application.dtos.request.UserCreateRequestDto;
//import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
//import com.i4u.user.application.dtos.response.UserDetailResponseDto;
//import com.i4u.user.application.dtos.response.UserListResponseDto;
//import com.i4u.common.domain.UserRole;
//import com.i4u.user.testconfig.UserControllerIntegrationTestConfig;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = UserController.class)
//@Import(UserControllerIntegrationTestConfig.class)
//class UserControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    @Qualifier("mockUserService")
//    private UserService userService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    private UserDetailResponseDto dummyUser() {
//        return UserDetailResponseDto.builder()
//                .userId(UUID.randomUUID())
//                .username("testuser")
//                .nickname("tester")
//                .email("test@example.com")
//                .slackId("slack-id")
//                .role("MASTER")
//                .isDeleted(false)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    @DisplayName("POST /api/v1/users - 사용자 생성")
//    void createUser() throws Exception {
//        UserCreateRequestDto requestDto = UserCreateRequestDto.builder()
//                .username("testuser")
//                .password("secure123")
//                .nickname("tester")
//                .email("test@example.com")
//                .slackId("slack-id")
//                .role(UserRole.MASTER)
//                .build();
//
//        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
//        when(userService.createUser(any(), anyString())).thenReturn(dummyUser());
//
//        mockMvc.perform(post("/api/v1/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.email").value("test@example.com"));
//    }
//
//    @Test
//    @DisplayName("GET /api/v1/users/{userId} - 사용자 조회")
//    void getUserById() throws Exception {
//        UUID userId = UUID.randomUUID();
//        when(userService.getUserById(eq(userId))).thenReturn(dummyUser());
//
//        mockMvc.perform(get("/api/v1/users/" + userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("사용자 조회 성공"));
//    }
//
//    @Test
//    @DisplayName("GET /api/v1/users/slack/{slackId} - Slack ID로 사용자 조회")
//    void getUserBySlackId() throws Exception {
//        String slackId = "slack-id";
//        when(userService.getUserBySlackId(eq(slackId))).thenReturn(dummyUser());
//
//        mockMvc.perform(get("/api/v1/users/slack/" + slackId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("사용자 조회 성공"));
//    }
//
//    @Test
//    @DisplayName("GET /api/v1/users/search - 사용자 검색")
//    void searchUsers() throws Exception {
//        UserListResponseDto listResponse = new UserListResponseDto(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));
//        when(userService.searchUsers(any(), any(), any())).thenReturn(listResponse);
//
//        mockMvc.perform(get("/api/v1/users/search")
//                        .param("keyword", "test")
//                        .param("role", "HUB_MANAGER")
//                        .header("X-User-Id", UUID.randomUUID().toString())
//                        .header("X-User-Role", "MASTER"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("사용자 검색 성공"));
//    }
//
//    @Test
//    @DisplayName("PUT /api/v1/users/{adminUserId}/{userId} - 사용자 정보 수정")
//    void updateUser() throws Exception {
//        UUID adminId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//
//        UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
//                .nickname("Updated")
//                .email("updated@example.com")
//                .role("USER")
//                .build();
//
//        when(userService.updateUser(eq(adminId), eq(userId), any())).thenReturn(dummyUser());
//
//        mockMvc.perform(put("/api/v1/users/" + adminId + "/" + userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("사용자 정보 업데이트 성공"));
//    }
//
//    @Test
//    @DisplayName("PUT /api/v1/users/{adminUserId}/role/{targetUserId} - 역할 변경")
//    void updateUserRole() throws Exception {
//        UUID adminId = UUID.randomUUID();
//        UUID targetId = UUID.randomUUID();
//        when(userService.updateUserRole(eq(adminId), eq(targetId), anyString())).thenReturn(dummyUser());
//
//        mockMvc.perform(put("/api/v1/users/" + adminId + "/role/" + targetId)
//                        .param("newRole", "HUB_MANAGER"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("사용자 역할 변경 성공"));
//    }
//
//    @Test
//    @DisplayName("DELETE /api/v1/users/{adminUserId}/{userId} - 사용자 삭제")
//    void deleteUser() throws Exception {
//        UUID adminId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID deletedBy = UUID.randomUUID();
//
//        when(userService.deleteUser(eq(adminId), eq(userId), eq(deletedBy))).thenReturn(dummyUser());
//
//        mockMvc.perform(delete("/api/v1/users/" + adminId + "/" + userId)
//                        .param("deletedBy", deletedBy.toString()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("사용자가 삭제되었습니다."));
//    }
//
//    @Test
//    @DisplayName("GET /api/v1/users/all - 전체 사용자 조회")
//    void getAllUsers() throws Exception {
//        UserListResponseDto listResponse = new UserListResponseDto(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));
//        when(userService.getAllUsers(any(Pageable.class))).thenReturn(listResponse);
//
//        mockMvc.perform(get("/api/v1/users/all")
//                        .param("page", "0")
//                        .param("size", "10")
//                        .header("X-User-Role", "MASTER"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("전체 사용자 조회 성공"));
//    }
//}