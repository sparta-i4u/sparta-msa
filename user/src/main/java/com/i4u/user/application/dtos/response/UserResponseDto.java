package com.i4u.user.application.dtos.response;

import com.i4u.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {

    private final Long userId;
    private final String username;
    private final String nickname;
    private final String email;
    private final String slackId;
    private final String role;
    private final boolean isDeleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // 정적 팩토리 메서드 - User 엔티티를 DTO로 변환
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .slackId(user.getSlackId())
                .role(user.getRole().name())
                .isDeleted(user.getIsDeleted()) // Soft Delete 여부 반영
                .createdAt(user.getCreatedAt()) // 생성 시간 반영
                .updatedAt(user.getUpdatedAt()) // 수정 시간 반영
                .build();
    }
}
