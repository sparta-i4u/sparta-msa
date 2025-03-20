package com.i4u.user.presentation;

import com.i4u.common.utils.CommonResponse;
import com.i4u.user.application.UserService;
import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
import com.i4u.user.application.dtos.request.UserSearchRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.dtos.response.UserListResponseDto;
import com.i4u.user.application.exception.UserException;
import com.i4u.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 (POST /users)
//    @PostMapping
//    public ResponseEntity<CommonResponse<UserDetailResponseDto>> createUser(
//            @RequestBody UserCreateRequestDto requestDto
//    ) {
//        UserDetailResponseDto userDetail = userService.createUser(requestDto);
//        return ResponseEntity.ok(CommonResponse.success(userDetail, "회원가입이 완료되었습니다."));
//    }

    // 특정 사용자 조회 - ID 기반 (GET /users/{userId})
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(CommonResponse.success(user, "사용자 조회 성공")))
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
    }

    // 특정 사용자 조회 - Slack ID 기반 (GET /users/slack/{slackId})
    @GetMapping("/slack/{slackId}")
    public ResponseEntity<CommonResponse<UserDetailResponseDto>> getUserBySlackId(@PathVariable String slackId) {
        return userService.getUserBySlackId(slackId)
                .map(user -> ResponseEntity.ok(CommonResponse.success(user, "사용자 조회 성공")))
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
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
}
