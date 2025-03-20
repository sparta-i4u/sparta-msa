package com.i4u.auth.domain;

import com.i4u.user.domain.UserRole;
import lombok.Getter;

@Getter
public enum AuthUserRole {
    MASTER("ROLE_MASTER"),  // 마스터 관리자
    HUB_MANAGER("ROLE_HUB_MANAGER"), // 허브 관리자
    DELIVERY("ROLE_DELIVERY_MANAGER"), // 배송 담당자
    COMPANY_MANAGER("ROLE_COMPANY_MANAGER"); // 업체 담당자

    private final String authority;

    AuthUserRole(String authority) {
        this.authority = authority;
    }

    public boolean isMaster() {
        return this == MASTER;
    }

    // ✅ UserRole과 매핑하는 메서드 추가
    public UserRole toUserRole() {
        return UserRole.valueOf(this.name()); // UserRole Enum과 동일한 이름을 사용
    }

    // ✅ 문자열을 AuthUserRole로 안전하게 변환하는 메서드 추가
    public static AuthUserRole fromString(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 역할입니다.");
        }
        try {
            return AuthUserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("존재하지 않는 역할입니다: " + role);
        }
    }
}
