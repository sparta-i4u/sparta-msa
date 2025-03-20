package com.i4u.auth.application.dtos.response;

import com.i4u.auth.domain.AuthUserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private String email;
    private AuthUserRole role; // ✅ String → Enum 타입으로 변경

    // ✅ 역할을 문자열로 반환하는 메서드 추가 (JSON 변환 시 사용 가능)
    public String getRoleAsString() {
        return role.name();
    }
}
