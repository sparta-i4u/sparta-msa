package com.i4u.user.domain;


import lombok.Getter;

/**
 * 사용자 역할(User Role) 관리 Enum
 * 각 역할은 "ROLE_"이 접두사로 붙은 권한 문자열을 가짐.
 */
@Getter
public enum UserRole {
    MASTER("ROLE_MASTER"),               // 마스터 관리자
    HUB_MANAGER("ROLE_HUB_MANAGER"),     // 허브 관리자
    DELIVERY("ROLE_DELIVERY_MANAGER"),           // 배송 담당자
    COMPANY_MANAGER("ROLE_COMPANY_MANAGER"); // 업체 담당자

    private final String authority; // 권한 문자열 (Spring Security 사용 가능)

    UserRole(String authority) {
        this.authority = authority;
    }

//    // 문자열을 UserRole Enum으로 변환하는 메서드
//    public static UserRole fromString(String role) {
//        try {
//            return UserRole.valueOf(role.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new AuthException.InvalidRoleException(role);
//        }
//    }
}
