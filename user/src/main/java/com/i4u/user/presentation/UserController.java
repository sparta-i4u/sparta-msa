package com.i4u.user.presentation;

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
    @PostMapping
    public ResponseEntity<UserDetailResponseDto> createUser(@RequestBody UserCreateRequestDto requestDto) {
        return ResponseEntity.ok(userService.createUser(requestDto));
    }

    // 특정 사용자 조회 - ID 기반 (GET /users/{userId})
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponseDto> getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
    }

    // 특정 사용자 조회 - Slack ID 기반 (GET /users/slack/{slackId})
    @GetMapping("/slack/{slackId}")
    public ResponseEntity<UserDetailResponseDto> getUserBySlackId(@PathVariable String slackId) {
        return userService.getUserBySlackId(slackId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
    }

    // 사용자 검색 (GET /users/search?keyword=xxx&role=HUB_MANAGER&page=0&size=10)
    @GetMapping("/search")
    public ResponseEntity<UserListResponseDto> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            Pageable pageable) {
        UserRole userRole = null;
        if (role != null && !role.isBlank()) {
            try {
                userRole = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new UserException(UserException.UserErrorType.INVALID_ROLE);
            }
        }
        UserSearchRequestDto searchRequest = new UserSearchRequestDto(keyword, userRole, pageable);
        return ResponseEntity.ok(userService.searchUsers(searchRequest));
    }

    // 사용자 정보 업데이트 (PUT /users/{adminUserId}/{userId})
    @PutMapping("/{adminUserId}/{userId}")
    public ResponseEntity<UserDetailResponseDto> updateUser(
            @PathVariable UUID adminUserId,
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(adminUserId, userId, requestDto));
    }

    // MASTER만 사용자의 ROLE 변경 가능 (PUT /users/{adminUserId}/role/{targetUserId})
    @PutMapping("/{adminUserId}/role/{targetUserId}")
    public ResponseEntity<UserDetailResponseDto> updateUserRole(
            @PathVariable UUID adminUserId,
            @PathVariable UUID targetUserId,
            @RequestParam String newRole) {
        return ResponseEntity.ok(userService.updateUserRole(adminUserId, targetUserId, newRole));
    }

    // 사용자 논리 삭제 - Soft Delete (DELETE /users/{adminUserId}/{userId})
    @DeleteMapping("/{adminUserId}/{userId}")
    public ResponseEntity<UserDetailResponseDto> deleteUser(
            @PathVariable UUID adminUserId,
            @PathVariable UUID userId,
            @RequestParam UUID deletedBy) {
        return ResponseEntity.ok(userService.deleteUser(adminUserId, userId, deletedBy));
    }
}
