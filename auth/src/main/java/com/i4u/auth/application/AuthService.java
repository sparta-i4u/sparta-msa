package com.i4u.auth.application;

import com.i4u.auth.application.dtos.request.AuthSignInRequestDto;
import com.i4u.auth.application.dtos.request.AuthSignUpRequestDto;
import com.i4u.auth.application.dtos.response.AuthResponseDto;
import com.i4u.auth.application.dtos.response.AuthUserInfoResponseDto;
import com.i4u.auth.domain.AuthUser;
import com.i4u.auth.domain.AuthUserRole;
import com.i4u.auth.domain.repository.AuthUserRepository;
import com.i4u.auth.infrastructure.client.UserClient;
import com.i4u.auth.infrastructure.security.JwtTokenProvider;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.auth.application.exception.AuthException;
import com.i4u.user.application.exception.UserException;
import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import com.i4u.user.domain.repository.UserRepository;
import com.i4u.user.infrastructure.security.aop.RequiresAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final UserClient userClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @RequiresAuth
    public AuthUserInfoResponseDto getAuthUserInfo(UUID userId) {
        // 데이터베이스에서 사용자 존재 여부 및 논리삭제 여부를 먼저 확인
        AuthUser authUser = authUserRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AuthException(AuthException.AuthErrorType.USER_NOT_FOUND));

        // 외부 UserClient를 통해 상세 정보를 조회
        AuthUserInfoResponseDto userDetail = userClient.getUserInfo(userId);

        return new AuthUserInfoResponseDto(
                userDetail.getUserId(),
                userDetail.getSlackId(),
                userDetail.getRole(),
                userDetail.isDeleted()
        );
    }

    // ✅ 회원가입 (User 서비스에 회원 정보 저장 & JWT 발급)
    public AuthResponseDto signUp(AuthSignUpRequestDto request) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 모듈로 회원 생성 요청
        UserCreateRequestDto userRequest = new UserCreateRequestDto(
                request.getUsername(),
                encodedPassword,
                request.getNickname(),
                request.getEmail(),
                request.getSlackId(),
                request.toUserRole()
        );

        // 💡 user-service 내부에서 중복 확인 등 처리
        UserDetailResponseDto userResponse = userClient.createUser(userRequest);
        UUID userId = userResponse.getUserId();

        // ❀ 수정: 중복 인코딩 제거 → rawPassword 전달
        // AuthUser 저장
        AuthUser authUser = AuthUser.createAuthUser(
                userId, userResponse.getEmail(), request.getPassword(), request.getSlackId(), request.getRole(), passwordEncoder
        );
        authUserRepository.save(authUser);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userId, userResponse.getEmail(), request.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(userId, userResponse.getEmail());

        return new AuthResponseDto(accessToken, refreshToken, userResponse.getEmail(), request.getRole());
    }

    // ✅ 로그인 (이메일 & 비밀번호 검증 후 JWT 토큰 발급)
    public AuthResponseDto signIn(AuthSignInRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AuthUser authUser = authUserRepository.findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new AuthException(AuthException.AuthErrorType.AUTHENTICATION_FAILED));

        String accessToken = jwtTokenProvider.createAccessToken(authUser.getUserId(), authUser.getEmail(), authUser.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(authUser.getUserId(), authUser.getEmail());

        log.info("✅ 로그인 성공: userId={}, email={}, role={}", authUser.getUserId(), authUser.getEmail(), authUser.getRole());

        return new AuthResponseDto(accessToken, refreshToken, authUser.getEmail(), authUser.getRole());
    }

    // ✅ 로그아웃 (JWT 블랙리스트 저장 가능)
    public void logout(String token) {
        log.info("✅ 로그아웃 완료: token={}", token);
    }

    // ✅ 토큰 검증
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    // ✅ 토큰 갱신 (리프레시 토큰을 사용하여 새로운 액세스 토큰 발급)
    public AuthResponseDto refreshToken(String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        AuthUser authUser = authUserRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new AuthException(AuthException.AuthErrorType.AUTHENTICATION_FAILED));

        String newAccessToken = jwtTokenProvider.createAccessToken(authUser.getUserId(), authUser.getEmail(), authUser.getRole().name());

        log.info("✅ 토큰 갱신 완료: userId={}, email={}", authUser.getUserId(), authUser.getEmail());

        return new AuthResponseDto(newAccessToken, refreshToken, authUser.getEmail(), authUser.getRole());
    }
}
