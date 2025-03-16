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

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 /users/sign-up
    @PostMapping("/sign-up")
    public ResponseEntity<UserDetailResponseDto> signUpUser(@RequestBody UserCreateRequestDto requestDto) {
        return ResponseEntity.ok(userService.createUser(requestDto));
    }

    // 특정 사용자 조회 - ID 기반 /users/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponseDto> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
    }

    // 특정 사용자 조회 - Slack ID 기반 /users/slack/{slackId}
    @GetMapping("/slack/{slackId}")
    public ResponseEntity<UserDetailResponseDto> getUserBySlackId(@PathVariable String slackId) {
        return userService.getUserBySlackId(slackId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
    }

    // 사용자 검색 - /users/search?keyword=xxx&role=CUSTOMER&page=0&size=10)
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

    // 사용자 정보 업데이트 /users/{userId})
    @PutMapping("/{adminUserId}/{userId}")
    public ResponseEntity<UserDetailResponseDto> updateUser(
            @PathVariable Long adminUserId,
            @PathVariable Long userId,
            @RequestBody UserUpdateRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(adminUserId, userId, requestDto));
    }

    // MASTER만 사용자의 ROLE을 변경 가능 /users/{adminUserId}/role/{targetUserId}
    @PutMapping("/{adminUserId}/role/{targetUserId}")
    public ResponseEntity<UserDetailResponseDto> updateUserRole(
            @PathVariable Long adminUserId,
            @PathVariable Long targetUserId,
            @RequestParam String newRole) {
        return ResponseEntity.ok(userService.updateUserRole(adminUserId, targetUserId, newRole));
    }

    // 사용자 논리 삭제 - Soft Delete /users/{userId}
    @DeleteMapping("/{adminUserId}/{userId}")
    public ResponseEntity<UserDetailResponseDto> deleteUser(
            @PathVariable Long adminUserId,
            @PathVariable Long userId,
            @RequestParam UUID deletedBy) {
        return ResponseEntity.ok(userService.deleteUser(adminUserId, userId, deletedBy));
    }
}
