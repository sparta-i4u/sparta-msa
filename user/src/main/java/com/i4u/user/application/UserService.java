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
import com.i4u.user.infrastructure.security.aop.RequiresMasterRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // UserRole직 변환 헬퍼 메서드 (예외 처리 개선)
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

    // 회원가입 (User 생성)
    public UserDetailResponseDto createUser(UserCreateRequestDto requestDTO) {
        if (userRepository.findBySlackIdAndIsDeletedFalse(requestDTO.getSlackId()).isPresent()) {
            throw new UserException(UserException.UserErrorType.DUPLICATE_USERNAME);
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        // 역할 변환 (convertToUserRole() 활용)
        UserRole userRole = convertToUserRole(requestDTO.getRole());

        // User 엔티티 생성 (DTO에서 변환하지 않고 User에서 처리)
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

    // 특정 사용자 조회 - ID 기반
    public Optional<UserDetailResponseDto> getUserById(Long userId) {
        return userRepository.findByUserIdAndIsDeletedFalse(userId)
                .map(UserDetailResponseDto::from);
    }

    // 특정 사용자 조회 - Slack ID 기반
    public Optional<UserDetailResponseDto> getUserBySlackId(String slackId) {
        return userRepository.findBySlackIdAndIsDeletedFalse(slackId)
                .map(UserDetailResponseDto::from);
    }

    // 사용자 검색 (닉네임, 이메일, 역할(Role) 기반 검색)
    public UserListResponseDto searchUsers(UserSearchRequestDto searchRequest) {
        Page<User> userPage = userRepository.searchUsers(
                searchRequest.getKeyword(),
                searchRequest.getRole(),
                searchRequest.getPageable(),
                false
        );
        return new UserListResponseDto(userPage);
    }

    // 사용자 정보 업데이트 (닉네임, 이메일 변경 가능, ROLE 변경 불가)
    public UserDetailResponseDto updateUser(Long adminUserId, Long userId, UserUpdateRequestDto requestDto) {
        User adminUser = userRepository.findByUserIdAndIsDeletedFalse(adminUserId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        boolean isAdmin = adminUser.getRole().isMaster();

        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        // 기존 값을 유지하면서 변경된 값만 적용
        UserUpdateRequestDto updatedDto = UserUpdateRequestDto.createUpdatedDto(user, requestDto);

        user.updateUser(updatedDto.getNickname(), updatedDto.getEmail(), adminUserId, isAdmin);

        userRepository.save(user);
        return UserDetailResponseDto.from(user);
    }

    // MASTER만 사용자의 ROLE을 변경 가능
    public UserDetailResponseDto updateUserRole(Long adminUserId, Long targetUserId, String newRole) {
        User adminUser = userRepository.findByUserIdAndIsDeletedFalse(adminUserId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        if (!adminUser.getRole().isMaster()) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        User targetUser = userRepository.findByUserIdAndIsDeletedFalse(targetUserId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        targetUser.updateRole(UserRole.valueOf(newRole.toUpperCase()), adminUser.getRole());
        userRepository.save(targetUser);

        return UserDetailResponseDto.from(targetUser);
    }

    // 사용자 논리 삭제 (Soft Delete)
    public UserDetailResponseDto deleteUser(Long adminUserId, Long userId, UUID deletedBy) {
        User adminUser = userRepository.findByUserIdAndIsDeletedFalse(adminUserId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        // MASTER 권한 확인
        if (!adminUser.getRole().isMaster()) {
            throw new UserException(UserException.UserErrorType.PERMISSION_DENIED);
        }

        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));

        user.softDelete(deletedBy);
        userRepository.save(user);

        return UserDetailResponseDto.from(user);
    }

//    @RequiresMasterRole
//    public UserDetailResponseDto updateUserRole(UUID adminUserId, UUID targetUserId, String newRole) {
//        User adminUser = userRepository.findByUserIdAndIsDeletedFalse(adminUserId)
//                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
//
//        User targetUser = userRepository.findByUserIdAndIsDeletedFalse(targetUserId)
//                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
//
//        targetUser.updateRole(UserRole.valueOf(newRole.toUpperCase()), adminUser.getRole());
//        userRepository.save(targetUser);
//
//        return UserDetailResponseDto.from(targetUser);
//    }
//
//    @RequiresMasterRole
//    public UserDetailResponseDto deleteUser(UUID adminUserId, UUID userId, UUID deletedBy) {
//        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
//                .orElseThrow(() -> new UserException(UserException.UserErrorType.USER_NOT_FOUND));
//
//        user.softDelete(deletedBy);
//        userRepository.save(user);
//
//        return UserDetailResponseDto.from(user);
//    }
}
