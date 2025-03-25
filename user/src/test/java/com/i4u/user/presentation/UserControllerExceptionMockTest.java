//package com.i4u.user.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.i4u.user.application.UserService;
//import com.i4u.user.application.exception.UserException;
//import com.i4u.user.application.exception.UserException.UserErrorType;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = UserController.class)
//class UserControllerExceptionMockTest {
//
//    @TestConfiguration
//    static class ExceptionTestConfig {
//        @Bean
//        public UserService userService() {
//            return Mockito.mock(UserService.class);
//        }
//    }
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserService userService;
//
//    @Test
//    @DisplayName("[예외] GET /api/v1/users/{userId} - 존재하지 않는 사용자")
//    void getUserById_notFound() throws Exception {
//        UUID userId = UUID.randomUUID();
//        when(userService.getUserById(userId)).thenThrow(new UserException(UserErrorType.USER_NOT_FOUND));
//
//        mockMvc.perform(get("/api/v1/users/" + userId))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다."));
//    }
//
//    @Test
//    @DisplayName("[예외] GET /api/v1/users/all - MASTER 권한 없이 요청")
//    void getAllUsers_permissionDenied() throws Exception {
//        mockMvc.perform(get("/api/v1/users/all")
//                        .header("X-User-Role", "USER"))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.message").value("권한이 없습니다."));
//    }
//
//    @Test
//    @DisplayName("[예외] PUT /api/v1/users/{adminUserId}/role/{targetUserId} - 잘못된 ROLE 입력")
//    void updateUserRole_invalidRole() throws Exception {
//        UUID adminId = UUID.randomUUID();
//        UUID targetId = UUID.randomUUID();
//
//        when(userService.updateUserRole(any(), any(), anyString()))
//                .thenThrow(new UserException(UserErrorType.INVALID_ROLE));
//
//        mockMvc.perform(put("/api/v1/users/" + adminId + "/role/" + targetId)
//                        .param("newRole", "INVALID_ROLE"))
//                .andExpect(status().is4xxClientError())
//                .andExpect(jsonPath("$.message").value("잘못된 사용자 역할입니다."));
//    }
//}