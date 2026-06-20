// 📌 UserDetailResponseDto.java 수정 (User 모듈)
package com.i4u.user.application.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.i4u.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserDetailResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private final String username;
    private final String nickname;
    private final String email;
    private final String slackId;
    private final String role;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT) // 기본값(false)인 경우 응답에서 생략
    private final boolean isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // ISO 8601 형식 적용
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime updatedAt;

    // ✅ User 객체를 DTO로 변환하는 메서드
    public static UserDetailResponseDto from(User user) {
        Objects.requireNonNull(user, "User 객체가 null일 수 없습니다.");

        return UserDetailResponseDto.builder()
                .userId(Objects.requireNonNull(user.getUserId(), "userId가 null일 수 없습니다."))
                .username(Objects.requireNonNull(user.getUsername(), "username이 null일 수 없습니다."))
                .nickname(user.getNickname())
                .email(Objects.requireNonNull(user.getEmail(), "email이 null일 수 없습니다."))
                .slackId(user.getSlackId())
                .role(Objects.requireNonNull(user.getRole(), "role이 null일 수 없습니다.").name()) // String 변환
                .isDeleted(user.getIsDeleted())
                .createdAt(Objects.requireNonNull(user.getCreatedAt(), "createdAt이 null일 수 없습니다."))
                .updatedAt(Objects.requireNonNull(user.getUpdatedAt(), "updatedAt이 null일 수 없습니다."))
                .build();
    }
}
