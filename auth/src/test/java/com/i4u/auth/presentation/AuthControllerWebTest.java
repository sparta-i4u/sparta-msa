//package com.i4u.auth.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.i4u.auth.application.AuthService;
//import com.i4u.auth.application.dtos.request.AuthSignInRequestDto;
//import com.i4u.auth.application.dtos.request.AuthSignUpRequestDto;
//import com.i4u.auth.application.dtos.response.AuthResponseDto;
//import com.i4u.auth.application.dtos.response.ConfirmUserResponse;
//import com.i4u.auth.domain.AuthUserRole;
//import com.i4u.common.utils.CommonResponse;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AuthController.class)
//@Import(TestAuthControllerConfig.class) // 👈 여기 추가!
//class AuthControllerWebTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void signUp_ShouldReturnAuthResponse_WhenValidRequest() throws Exception {
//        // Given
//        AuthSignUpRequestDto request = AuthSignUpRequestDto.builder()
//                .username("testuser")
//                .password("test1234!")
//                .nickname("닉네임")
//                .email("test@example.com")
//                .slackId("slack001")
//                .role(AuthUserRole.MASTER)
//                .build();
//
//        AuthResponseDto mockResponse = new AuthResponseDto(
//                "access-token",
//                "refresh-token",
//                request.getEmail(),
//                request.getRole()
//        );
//
//        when(authService.signUp(request)).thenReturn(mockResponse);
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/auth/sign-up")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
//                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
//                .andExpect(jsonPath("$.data.email").value(request.getEmail()))
//                .andExpect(jsonPath("$.data.role").value(request.getRole().name()))
//                .andExpect(jsonPath("$.message").value("회원가입 성공"));
//    }
//
//    @Test
//    void signIn_ShouldReturnAuthResponse_WhenValidCredentials() throws Exception {
//        // Given
//        AuthSignInRequestDto request = new AuthSignInRequestDto("test@example.com", "password123");
//
//        AuthResponseDto mockResponse = new AuthResponseDto(
//                "access-token",
//                "refresh-token",
//                request.getEmail(),
//                AuthUserRole.MASTER
//        );
//
//        when(authService.signIn(request)).thenReturn(mockResponse);
//
//        // When & Then
//        mockMvc.perform(post("/api/v1/auth/sign-in")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
//                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
//                .andExpect(jsonPath("$.data.email").value(request.getEmail()))
//                .andExpect(jsonPath("$.data.role").value("MASTER"))
//                .andExpect(jsonPath("$.message").value("로그인 성공"));
//    }
//
//    @Test
//    void validateToken_ShouldReturnTrue_WhenTokenIsValid() throws Exception {
//        // Given
//        String token = "valid-token";
//        when(authService.validateToken(token)).thenReturn(true);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/auth/validate-token")
//                        .param("token", token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value(true))
//                .andExpect(jsonPath("$.message").value("토큰 검증 결과"));
//    }
//    @Test
//    void refreshToken_ShouldReturnNewAccessToken() throws Exception {
//        String refreshToken = "refresh-token-123";
//        AuthResponseDto response = new AuthResponseDto("new-access-token", refreshToken, "test@example.com", AuthUserRole.MASTER);
//
//        when(authService.refreshToken(refreshToken)).thenReturn(response);
//
//        mockMvc.perform(post("/api/v1/auth/refresh-token")
//                        .param("refreshToken", refreshToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
//                .andExpect(jsonPath("$.data.refreshToken").value(refreshToken))
//                .andExpect(jsonPath("$.data.email").value("test@example.com"))
//                .andExpect(jsonPath("$.data.role").value("USER"))
//                .andExpect(jsonPath("$.message").value("토큰 갱신 성공"));
//    }
//
//    @Test
//    void logout_ShouldReturnSuccessMessage() throws Exception {
//        String token = "logout-token";
//
//        mockMvc.perform(post("/api/v1/auth/logout")
//                        .param("token", token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("로그아웃 성공"));
//    }
//
//    @Test
//    void getAuthUserInfo_ShouldReturnUserInfo() throws Exception {
//        UUID userId = UUID.randomUUID();
//
//        ConfirmUserResponse response = ConfirmUserResponse.builder()
//                .userId(userId)
//                .userSlackId("slack-id-001")
//                .userRole("ROLE_MASTER")
//                .email("admin@example.com")
//                .isDeleted(false)
//                .build();
//
//        when(authService.getAuthUserInfo(userId)).thenReturn(response);
//
//        mockMvc.perform(get("/api/v1/auth/user-info/" + userId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value(userId.toString()))
//                .andExpect(jsonPath("$.userSlackId").value("slack-id-001"))
//                .andExpect(jsonPath("$.userRole").value("ROLE_MASTER"))
//                .andExpect(jsonPath("$.email").value("admin@example.com"))
//                .andExpect(jsonPath("$.isDeleted").value(false));
//    }
//}