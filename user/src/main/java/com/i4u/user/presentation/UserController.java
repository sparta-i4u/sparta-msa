package com.i4u.user.presentation;

import com.i4u.common.utils.CommonResponse;
import com.i4u.user.application.UserService;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
import com.i4u.user.application.dtos.request.UserSearchRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.dtos.response.UserListResponseDto;
import com.i4u.user.application.exception.UserException;
import com.i4u.common.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserApi{

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    // 회원가입 (POST /users)
    @PostMapping
    public ResponseEntity<UserDetailResponseDto> createUser(@RequestBody UserCreateRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        UserDetailResponseDto responseDto = userService.createUser(requestDto, requestDto.getPassword());
        return ResponseEntity.ok(responseDto);
    }

    // 특정 사용자 조회 - ID 기반 (GET /users/{userId})
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserById(@PathVariable UUID userId) {
        UserDetailResponseDto user = userService.getUserById(userId);
        return ResponseEntity.ok(CommonResponse.success(user, "사용자 조회 성공"));
    }

    // 특정 사용자 조회 - Slack ID 기반 (GET /users/slack/{slackId})
    @GetMapping("/slack/{slackId}")
    public ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserBySlackId(@PathVariable String slackId) {
        UserDetailResponseDto user = userService.getUserBySlackId(slackId);
        return ResponseEntity.ok(CommonResponse.success(user, "사용자 조회 성공"));
    }

    // 사용자 검색 (GET /users/search?keyword=xxx&role=HUB_MANAGER&page=0&size=10)
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<UserListResponseDto>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            Pageable pageable,
            @RequestHeader("X-User-Id") UUID requestUserId,
            @RequestHeader("X-User-Role") String requestUserRole) {

        UserRole userRole = UserRole.valueOf(requestUserRole.toUpperCase());
        UserSearchRequestDto searchRequest = new UserSearchRequestDto(keyword, userRole, pageable);
        return ResponseEntity.ok(CommonResponse.success(
                userService.searchUsers(searchRequest, requestUserId, userRole), "사용자 검색 성공"));
    }

    // 사용자 정보 업데이트 (PUT /users/{adminUserId}/{userId})
    @PutMapping("/{adminUserId}/{userId}")
    public ResponseEntity<CommonResponse<UserDetailResponseDto>> updateUser(
            @PathVariable UUID adminUserId,
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequestDto requestDto) {
        return ResponseEntity.ok(CommonResponse.success(
                userService.updateUser(adminUserId, userId, requestDto),
                "사용자 정보 업데이트 성공")
        );
    }

    // MASTER만 사용자의 ROLE 변경 가능 (PUT /users/{adminUserId}/role/{targetUserId})
    @PutMapping("/{adminUserId}/role/{targetUserId}")
    public ResponseEntity<CommonResponse<UserDetailResponseDto>> updateUserRole(
            @PathVariable UUID adminUserId,
            @PathVariable UUID targetUserId,
            @RequestParam String newRole) {
        return ResponseEntity.ok(CommonResponse.success(
                userService.updateUserRole(adminUserId, targetUserId, newRole),
                "사용자 역할 변경 성공")
        );
    }

    // 사용자 논리 삭제 - Soft Delete (DELETE /users/{adminUserId}/{userId})
    @DeleteMapping("/{adminUserId}/{userId}")
    public ResponseEntity<CommonResponse<UserDetailResponseDto>> deleteUser(
            @PathVariable UUID adminUserId,
            @PathVariable UUID userId,
            @RequestParam UUID deletedBy) {
        return ResponseEntity.ok(CommonResponse.success(
                userService.deleteUser(adminUserId, userId, deletedBy),
                "사용자가 삭제되었습니다.")
        );
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<UserListResponseDto>> getAllUsers(
            @RequestHeader("X-User-Role") String userRole,
            Pageable pageable) {

        if (!UserRole.MASTER.name().equalsIgnoreCase(userRole)) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        return ResponseEntity.ok(CommonResponse.success(
                userService.getAllUsers(pageable),
                "전체 사용자 조회 성공"
        ));
    }
}
