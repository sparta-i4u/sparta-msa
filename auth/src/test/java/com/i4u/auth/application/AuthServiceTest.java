package com.i4u.auth.application;

import com.i4u.auth.application.dtos.request.AuthSignInRequestDto;
import com.i4u.auth.application.dtos.request.AuthSignUpRequestDto;
import com.i4u.auth.application.dtos.response.AuthResponseDto;
import com.i4u.auth.application.dtos.response.ConfirmUserResponse;
import com.i4u.auth.application.exception.AuthException;
import com.i4u.auth.domain.AuthUser;
import com.i4u.auth.domain.AuthUserRole;
import com.i4u.auth.domain.repository.AuthUserRepository;
import com.i4u.auth.infrastructure.client.UserClient;
import com.i4u.auth.infrastructure.security.JwtTokenProvider;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private AuthUserRepository authUserRepository;
    private UserClient userClient;
    private JwtTokenProvider jwtTokenProvider;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        authUserRepository = mock(AuthUserRepository.class);
        userClient = mock(UserClient.class);
        jwtTokenProvider = mock(JwtTokenProvider.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);

        authService = new AuthService(
                authUserRepository,
                userClient,
                jwtTokenProvider,
                passwordEncoder,
                authenticationManager
        );
    }

    @Test
    void signUp_ShouldCreateUserAndReturnTokens() {
        // Given
        AuthSignUpRequestDto request = new AuthSignUpRequestDto(
                "testuser",
                "plainPassword",
                "nickname",
                "test@example.com",
                "slack123",
                AuthUserRole.MASTER
        );

        UUID fakeUserId = UUID.randomUUID();
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        when(userClient.createUser(any(UserCreateRequestDto.class)))
                .thenReturn(new UserDetailResponseDto(
                        fakeUserId,
                        request.getUsername(),
                        request.getNickname(),
                        request.getEmail(),
                        request.getSlackId(),
                        request.getRole().name(), // ✅ String으로 변환
                        false,                    // isDeleted
                        LocalDateTime.now(),     // createdAt
                        LocalDateTime.now()      // updatedAt
                ));

        when(jwtTokenProvider.createAccessToken(any(), any(), any())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any(), any())).thenReturn("refresh-token");

        // When
        AuthResponseDto response = authService.signUp(request);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getRole()).isEqualTo(request.getRole());

        verify(authUserRepository).save(any(AuthUser.class));
        verify(userClient).createUser(any(UserCreateRequestDto.class));
    }

    @Test
    void signIn_ShouldAuthenticateAndReturnTokens() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        UUID userId = UUID.randomUUID();
        AuthUserRole role = AuthUserRole.MASTER;

        AuthSignInRequestDto request = new AuthSignInRequestDto(email, password);

        AuthUser mockUser = AuthUser.builder()
                .userId(userId)
                .email(email)
                .password("encodedPassword")
                .role(role)
                .slackId("slack123")
                .isDeleted(false)
                .build();

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(authUserRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(java.util.Optional.of(mockUser));
        when(jwtTokenProvider.createAccessToken(userId, email, role.name())).thenReturn("mock-access-token");
        when(jwtTokenProvider.createRefreshToken(userId, email)).thenReturn("mock-refresh-token");

        // When
        AuthResponseDto response = authService.signIn(request);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("mock-refresh-token");
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getRole()).isEqualTo(role);

        verify(authenticationManager).authenticate(any());
        verify(authUserRepository).findByEmailAndIsDeletedFalse(email);
        verify(jwtTokenProvider).createAccessToken(userId, email, role.name());
        verify(jwtTokenProvider).createRefreshToken(userId, email);
    }


    @Test
    void signIn_ShouldThrowException_WhenUserNotFound() {
        // Given
        String email = "notfound@example.com";
        String password = "wrongpassword";
        AuthSignInRequestDto request = new AuthSignInRequestDto(email, password);

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(authUserRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.signIn(request))
                .isInstanceOf(AuthException.class);

        verify(authUserRepository).findByEmailAndIsDeletedFalse(email);
    }


    @Test
    void getAuthUserInfo_ShouldReturnUserInfo_WhenUserExists() {
        // Given
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";
        String slackId = "slack123";
        AuthUserRole role = AuthUserRole.HUB_MANAGER;

        AuthUser mockUser = AuthUser.builder()
                .userId(userId)
                .email(email)
                .password("encodedPassword")
                .role(role)
                .slackId(slackId)
                .isDeleted(false)
                .build();

        when(authUserRepository.findByUserIdAndIsDeletedFalse(userId)).thenReturn(Optional.of(mockUser));

        // When
        ConfirmUserResponse response = authService.getAuthUserInfo(userId);

        // Then
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getUserSlackId()).isEqualTo(slackId);
        assertThat(response.getUserRole()).isEqualTo(role.getAuthority());
        assertThat(response.getIsDeleted()).isFalse();

        verify(authUserRepository).findByUserIdAndIsDeletedFalse(userId);
    }
    @Test
    void refreshToken_ShouldGenerateNewAccessToken() {
        // Given
        String refreshToken = "valid-refresh-token";
        String email = "user@example.com";
        UUID userId = UUID.randomUUID();
        AuthUserRole role = AuthUserRole.COMPANY_MANAGER;

        AuthUser mockUser = AuthUser.builder()
                .userId(userId)
                .email(email)
                .password("encoded")
                .role(role)
                .slackId("slack001")
                .isDeleted(false)
                .build();

        when(jwtTokenProvider.getEmailFromToken(refreshToken)).thenReturn(email);
        when(authUserRepository.findByEmailAndIsDeletedFalse(email)).thenReturn(Optional.of(mockUser));
        when(jwtTokenProvider.createAccessToken(userId, email, role.name())).thenReturn("new-access-token");

        // When
        AuthResponseDto response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getRole()).isEqualTo(role);

        verify(jwtTokenProvider).getEmailFromToken(refreshToken);
        verify(authUserRepository).findByEmailAndIsDeletedFalse(email);
        verify(jwtTokenProvider).createAccessToken(userId, email, role.name());
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        String token = "valid-token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);

        // When
        boolean result = authService.validateToken(token);

        // Then
        assertThat(result).isTrue();
        verify(jwtTokenProvider).validateToken(token);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Given
        String token = "invalid-token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // When
        boolean result = authService.validateToken(token);

        // Then
        assertThat(result).isFalse();
        verify(jwtTokenProvider).validateToken(token);
    }
}
