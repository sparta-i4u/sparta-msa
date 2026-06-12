package com.i4u.auth.domain;

import lombok.Getter;

@Getter
public enum AuthUserRole {
    MASTER("ROLE_MASTER"),
    HUB_MANAGER("ROLE_HUB_MANAGER"),
    DELIVERY("ROLE_DELIVERY_MANAGER"),
    COMPANY_MANAGER("ROLE_COMPANY_MANAGER");

    private final String authority;

    AuthUserRole(String authority) {
        this.authority = authority;
    }

    public boolean isMaster() {
        return this == MASTER;
    }

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
