package com.i4u.user.application.dtos.response;

import com.i4u.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder

@AllArgsConstructor
public class UserDetailResponseDto {

    private final Long userId;
    private final String username;
    private final String nickname;
    private final String email;
    private final String slackId;
    private final String role;
    private final boolean isDeleted;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static UserDetailResponseDto from(User user) {
        return UserDetailResponseDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .slackId(user.getSlackId())
                .role(user.getRole().name())
                .isDeleted(user.getIsDeleted())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
