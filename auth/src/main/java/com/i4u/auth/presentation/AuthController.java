package com.i4u.auth.presentation;

import com.i4u.auth.application.AuthService;
import com.i4u.auth.application.dtos.request.AuthSignInRequestDto;
import com.i4u.auth.application.dtos.request.AuthSignUpRequestDto;
import com.i4u.auth.application.dtos.response.AuthResponseDto;
import com.i4u.auth.application.dtos.response.AuthUserInfoResponseDto;
import com.i4u.auth.application.dtos.response.ConfirmUserResponse;
import com.i4u.common.utils.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    // ✅ 사용자 정보 조회 API (다른 모듈에서 사용 가능)
    @GetMapping("/user-info/{userId}")
    public ConfirmUserResponse getAuthUserInfo(@PathVariable UUID userId) {
        System.out.println("userId : " + userId);
        return authService.getAuthUserInfo(userId);
    }

    // 회원가입 API
    @PostMapping("/sign-up")
    public ResponseEntity<CommonResponse<AuthResponseDto>> signUp(@RequestBody AuthSignUpRequestDto request) {
        return ResponseEntity.ok(CommonResponse.success(authService.signUp(request), "회원가입 성공"));
    }

    // 로그인 API
    @PostMapping("/sign-in")
    public ResponseEntity<CommonResponse<AuthResponseDto>> signIn(@RequestBody AuthSignInRequestDto request) {
        return ResponseEntity.ok(CommonResponse.success(authService.signIn(request), "로그인 성공"));
    }

    // 로그아웃 API
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(@RequestParam String token) {
        authService.logout(token);
        return ResponseEntity.ok(CommonResponse.success(null, "로그아웃 성공"));
    }

    // JWT 토큰 검증 API
    @GetMapping("/validate-token")
    public ResponseEntity<CommonResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(CommonResponse.success(isValid, "토큰 검증 결과"));
    }

    // 토큰 갱신 API
    @PostMapping("/refresh-token")
    public ResponseEntity<CommonResponse<AuthResponseDto>> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(CommonResponse.success(authService.refreshToken(refreshToken), "토큰 갱신 성공"));
    }
}
