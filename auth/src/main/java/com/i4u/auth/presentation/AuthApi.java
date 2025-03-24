package com.i4u.auth.presentation;

import com.i4u.auth.application.dtos.request.AuthSignInRequestDto;
import com.i4u.auth.application.dtos.request.AuthSignUpRequestDto;
import com.i4u.auth.application.dtos.response.AuthResponseDto;
import com.i4u.auth.application.dtos.response.ConfirmUserResponse;
import com.i4u.common.utils.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰 관련 API")
public interface AuthApi {

    @Operation(summary = "사용자 정보 조회", description = "userId를 기반으로 사용자 인증 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = ConfirmUserResponse.class)))
    ConfirmUserResponse getAuthUserInfo(UUID userId);

    @Operation(summary = "회원가입", description = "새로운 계정을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class)))
    ResponseEntity<CommonResponse<AuthResponseDto>> signUp(AuthSignUpRequestDto request);

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class)))
    ResponseEntity<CommonResponse<AuthResponseDto>> signIn(AuthSignInRequestDto request);

    @Operation(summary = "로그아웃", description = "토큰을 무효화하고 로그아웃 처리합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공",
            content = @Content(schema = @Schema(implementation = CommonResponse.class)))
    ResponseEntity<CommonResponse<Void>> logout(String token);

    @Operation(summary = "토큰 유효성 검증", description = "JWT 토큰이 유효한지 검증합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 검증 성공",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    ResponseEntity<CommonResponse<Boolean>> validateToken(String token);

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 이용해 새로운 액세스 토큰을 발급합니다.")
    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class)))
    ResponseEntity<CommonResponse<AuthResponseDto>> refreshToken(String refreshToken);
}
