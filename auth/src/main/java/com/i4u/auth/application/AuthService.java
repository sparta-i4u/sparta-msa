package com.i4u.auth.application;

import com.i4u.auth.application.dtos.request.AuthSignInRequestDto;
import com.i4u.auth.application.dtos.request.AuthSignUpRequestDto;
import com.i4u.auth.application.dtos.response.AuthResponseDto;
import com.i4u.auth.domain.AuthUser;
import com.i4u.auth.domain.repository.AuthUserRepository;
import com.i4u.auth.infrastructure.client.UserClient;
import com.i4u.auth.infrastructure.security.JwtTokenProvider;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.auth.application.exception.AuthException;
import com.i4u.user.application.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // ✅ 특정 필드 기준으로 사용자 정보를 조회하는 메서드 (모든 모듈에서 사용 가능)
    public Map<String, Object> getUserInfo(UUID userId, List<String> fields) {
        Map<String, Object> userData = userClient.getUserInfo(userId, fields);

        if (userData == null || userData.isEmpty()) {
            throw new UserException(UserException.UserErrorType.USER_NOT_FOUND);
        }

        Map<String, Object> userInfo = new HashMap<>();
        if (fields == null || fields.isEmpty()) {
            // 기본적으로 모든 데이터 반환
            userInfo.put("userId", userData.get("userId"));
            userInfo.put("userSlackId", userData.get("userSlackId"));
            userInfo.put("isDeleted", userData.get("isDeleted"));
        } else {
            if (fields.contains("userId")) userInfo.put("userId", userData.get("userId"));
            if (fields.contains("userSlackId")) userInfo.put("userSlackId", userData.get("userSlackId"));
            if (fields.contains("isDeleted")) userInfo.put("isDeleted", userData.get("isDeleted"));
        }
        return userInfo;
    }

    // 회원가입 (User 서비스에 회원 정보 저장 & JWT 발급)
    public AuthResponseDto signUp(AuthSignUpRequestDto request) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 서비스로 회원 정보 저장 요청
        UserCreateRequestDto userRequest = new UserCreateRequestDto(
                request.getUsername(),
                request.getNickname(),
                request.getEmail(),
                request.getSlackId(),
                request.toUserRole()
        );

        // User 서비스 호출 (`userId` 포함)
        UserDetailResponseDto userResponse = userClient.createUser(userRequest, encodedPassword);
        UUID userId = userResponse.getUserId(); // `userId` 가져오기

        // AuthUser 저장 (`userId` 포함)
        AuthUser authUser = AuthUser.createAuthUser(
                userId, userResponse.getEmail(), encodedPassword, request.getRole(), passwordEncoder
        );
        authUserRepository.save(authUser);

        // JWT 토큰 발급 (`userId` 포함)
        String accessToken = jwtTokenProvider.createAccessToken(UUID.fromString(userId.toString()), userResponse.getEmail(), request.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(UUID.fromString(userId.toString()), userResponse.getEmail());

        log.info("✅ 회원가입 완료: userId={}, email={}, role={}", userId, userResponse.getEmail(), request.getRole().name());

        return new AuthResponseDto(accessToken, refreshToken, userResponse.getEmail(), request.getRole().name());
    }

    // 로그인 (이메일 & 비밀번호 검증 후 JWT 토큰 발급)
    public AuthResponseDto signIn(AuthSignInRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 로그인한 사용자 정보 조회 (`userId` 포함)
        AuthUser authUser = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(AuthException.AuthErrorType.AUTHENTICATION_FAILED));

        // JWT 토큰 발급 (`userId` 포함)
        String accessToken = jwtTokenProvider.createAccessToken(UUID.fromString(authUser.getUserId().toString()), authUser.getEmail(), authUser.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(UUID.fromString(authUser.getUserId().toString()), authUser.getEmail());

        log.info("✅ 로그인 성공: userId={}, email={}, role={}", authUser.getUserId(), authUser.getEmail(), authUser.getRole().name());

        return new AuthResponseDto(accessToken, refreshToken, authUser.getEmail(), authUser.getRole().name());
    }

    // 로그아웃 (JWT 블랙리스트 저장 가능)
    public void logout(String token) {
        // JWT 블랙리스트 관리 기능을 사용할 경우, 토큰 저장 가능
        log.info("✅ 로그아웃 완료: token={}", token);
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    // 토큰 갱신 (리프레시 토큰을 사용하여 새로운 액세스 토큰 발급)
    public AuthResponseDto refreshToken(String refreshToken) {
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // 리프레시 토큰으로 사용자 정보 조회 (`userId` 포함)
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthException.AuthErrorType.AUTHENTICATION_FAILED));

        // 새로운 액세스 토큰 발급 (`userId` 포함)
        String newAccessToken = jwtTokenProvider.createAccessToken(UUID.fromString(authUser.getUserId().toString()), authUser.getEmail(), authUser.getRole().name());

        log.info("✅ 토큰 갱신 완료: userId={}, email={}", authUser.getUserId(), authUser.getEmail());

        return new AuthResponseDto(newAccessToken, refreshToken, authUser.getEmail(), authUser.getRole().name());
    }
}
