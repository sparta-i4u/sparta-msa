package com.i4u.user.presentation;

import com.i4u.common.utils.CommonResponse;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.dtos.response.UserListResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "사용자 API", description = "사용자 관련 API")
public interface UserApi {

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 등록 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
    ResponseEntity<UserDetailResponseDto> createUser(UserCreateRequestDto requestDto);

    @Operation(summary = "사용자 ID로 조회", description = "UUID 기반으로 사용자를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
    ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserById(UUID userId);

    @Operation(summary = "Slack ID로 사용자 조회", description = "Slack ID 기반으로 사용자를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
    ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserBySlackId(String slackId);

    @Operation(summary = "사용자 검색", description = "검색 조건에 따라 사용자를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 검색 성공",
            content = @Content(schema = @Schema(implementation = UserListResponseDto.class)))
    ResponseEntity<CommonResponse<UserListResponseDto>> searchUsers(
            String keyword,
            String role,
            Pageable pageable,
            UUID requestUserId,
            String requestUserRole
    );

    @Operation(summary = "사용자 정보 수정", description = "관리자가 특정 사용자의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
    ResponseEntity<CommonResponse<UserDetailResponseDto>> updateUser(
            UUID adminUserId,
            UUID userId,
            UserUpdateRequestDto requestDto
    );

    @Operation(summary = "사용자 역할 변경", description = "MASTER만 특정 사용자의 역할을 변경할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "사용자 역할 변경 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
    ResponseEntity<CommonResponse<UserDetailResponseDto>> updateUserRole(
            UUID adminUserId,
            UUID targetUserId,
            String newRole
    );

    @Operation(summary = "사용자 삭제", description = "사용자를 논리적으로 삭제합니다 (Soft Delete).")
    @ApiResponse(responseCode = "200", description = "사용자 삭제 성공",
            content = @Content(schema = @Schema(implementation = UserDetailResponseDto.class)))
    ResponseEntity<CommonResponse<UserDetailResponseDto>> deleteUser(
            UUID adminUserId,
            UUID userId,
            UUID deletedBy
    );

    @Operation(summary = "전체 사용자 조회", description = "모든 사용자 정보를 조회합니다. MASTER 권한 필요.")
    @ApiResponse(responseCode = "200", description = "전체 사용자 조회 성공",
            content = @Content(schema = @Schema(implementation = UserListResponseDto.class)))
    ResponseEntity<CommonResponse<UserListResponseDto>> getAllUsers(
            String userRole,
            Pageable pageable
    );
}
