package com.i4u.user.domain;

import lombok.Getter;

// 사용자 역할(User Role) 관리 Enum
@Getter
public enum UserRole {
    MASTER("ROLE_MASTER"),                 // 마스터 관리자
    HUB_MANAGER("ROLE_HUB_MANAGER"),       // 허브 관리자
    DELIVERY("ROLE_DELIVERY_MANAGER"),     // 배송 담당자
    COMPANY_MANAGER("ROLE_COMPANY_MANAGER"); // 업체 담당자

    private final String authority; // 권한 문자열 (Spring Security 사용 가능)

    UserRole(String authority) {
        this.authority = authority;
    }

    // 회원가입 시 역할 설정 (모든 역할 선택 가능)
    public static UserRole fromString(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("역할은 필수 입력값입니다.");
        }
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바른 역할을 입력해주세요.");
        }
    }

    // MASTER 권한 여부 확인
    public boolean isMaster() {
        return this == MASTER;
    }
}
