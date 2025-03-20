package com.i4u.auth.application.dtos.response;

import com.i4u.auth.domain.AuthUserRole;
import com.i4u.user.application.dtos.response.UserDetailResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserInfoResponseDto {

    private UUID userId;
    private String slackId;
    private AuthUserRole role;
    private boolean isDeleted;

    public static AuthUserInfoResponseDto from(UserDetailResponseDto userDetail) {
        return new AuthUserInfoResponseDto(
                userDetail.getUserId(),
                userDetail.getSlackId(),
                AuthUserRole.valueOf(userDetail.getRole()), // ✅ 변환 일관성 유지
                userDetail.isDeleted()
        );
    }
}
