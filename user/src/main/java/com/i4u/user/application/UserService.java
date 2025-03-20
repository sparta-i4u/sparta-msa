package com.i4u.user.application;

import com.i4u.user.application.dtos.request.UserCreateRequestDto;
import com.i4u.user.application.dtos.request.UserUpdateRequestDto;
import com.i4u.user.application.dtos.request.UserSearchRequestDto;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import com.i4u.user.application.dtos.response.UserListResponseDto;
import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import com.i4u.user.domain.repository.UserRepository;
import com.i4u.user.application.exception.UserException;

// RequiresMasterRole 어노테이션을 Auth 모듈에서 User 모듈로 다시 가져오도록 설정

import com.i4u.user.infrastructure.security.aop.RequiresMasterRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // ✅ 역할 문자열을 UserRole Enum으로 변환하는 유틸리티 메서드
    private UserRole convertToUserRole(String role) {
        if (role == null || role.isBlank()) {
            throw new UserException(UserException.UserErrorType.INVALID_ROLE);
        }
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserException(UserException.UserErrorType.INVALID_ROLE);
        }
    }

    // ✅ 신규 사용자 생성 메서드
    public UserDetailResponseDto createUser(UserCreateRequestDto requestDTO, String encodedPassword) {
        if (userRepository.findBySlackIdAndIsDeletedFalse(requestDTO.getSlackId()).isPresent()) {
            throw new UserException(UserException.UserErrorType.DUPLICATE_USERNAME);
        }

        UserRole userRole = requestDTO.getRole();

        User newUser = User.createUser(
                requestDTO.getUsername(),
                encodedPassword,
                requestDTO.getNickname(),
                requestDTO.getEmail(),
                requestDTO.getSlackId(),
                userRole
        );
        userRepository.save(newUser);
        return UserDetailResponseDto.from(newUser);
    }

    // ✅ userId를 기반으로 사용자 조회
    public Optional<UserDetailResponseDto> getUserById(UUID userId) {
        return userRepository.findByUserIdAndIsDeletedFalse(userId)
                .map(UserDetailResponseDto::from)
                .or(() -> {
                    throw new UserException(UserException.UserErrorType.USER_NOT_FOUND);
                });
    }

    // ✅ Slack ID를 기반으로 사용자 조회
    public Optional<UserDetailResponseDto> getUserBySlackId(String slackId) {
        return userRepository.findBySlackIdAndIsDeletedFalse(slackId)
                .map(UserDetailResponseDto::from)
                .or(() -> {
                    throw new UserException(UserException.UserErrorType.USER_NOT_FOUND);
                });
    }

    // ✅ 사용자 검색 기능 (페이징 포함)
    public UserListResponseDto searchUsers(UserSearchRequestDto searchRequest, UUID requestUserId, UserRole requestUserRole) {
        boolean isMaster = requestUserRole == UserRole.MASTER;

        Page<User> userPage;
        if (isMaster) {
            // MASTER는 모든 사용자 검색 가능
            userPage = userRepository.searchUsers(
                    searchRequest.getKeyword(),
                    searchRequest.getRole(),
                    searchRequest.getPageable(),
                    false
            );
        } else {
            // 허브 관리자, 배송 담당자, 업체 담당자는 본인만 검색 가능
            userPage = userRepository.searchUsers(
                    searchRequest.getKeyword(),
                    requestUserRole,
                    searchRequest.getPageable(),
                    false
            ).map(user -> user.getUserId().equals(requestUserId) ? user : null); // 본인만 검색 가능
        }
        return new UserListResponseDto(userPage);
    }

    // ✅ 사용자 정보 업데이트 (관리자 권한 필요 여부 체크 포함)
    public UserDetailResponseDto updateUser(UUID adminUserId, UUID userId, UserUpdateRequestDto requestDto) {
        User adminUser = userRepository.findByUserIdAndIsDeletedFalse(adminUserId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        boolean isAdmin = adminUser.getRole().isMaster();

        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        UserUpdateRequestDto updatedDto = UserUpdateRequestDto.createUpdatedDto(user, requestDto);

        user.updateUser(updatedDto.getNickname(), updatedDto.getEmail(), adminUserId, isAdmin);

        userRepository.save(user);
        return UserDetailResponseDto.from(user);
    }

    // ✅ 사용자 역할 변경 (MASTER 권한 필요)
    @RequiresMasterRole
    public UserDetailResponseDto updateUserRole(UUID adminUserId, UUID targetUserId, String newRole) {
        User targetUser = userRepository.findByUserIdAndIsDeletedFalse(targetUserId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        targetUser.updateRole(UserRole.valueOf(newRole.toUpperCase()), UserRole.MASTER);
        userRepository.save(targetUser);

        return UserDetailResponseDto.from(targetUser);
    }

    // ✅ 사용자 논리 삭제 (Soft Delete, MASTER 권한 필요)
    @RequiresMasterRole
    public UserDetailResponseDto deleteUser(UUID adminUserId, UUID userId, UUID deletedBy) {
        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        user.softDelete(deletedBy);
        userRepository.save(user);

        return UserDetailResponseDto.from(user);
    }
}