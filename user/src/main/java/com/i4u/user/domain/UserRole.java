package com.i4u.user.domain;

import com.i4u.user.application.exception.UserException;
import lombok.Getter;

@Getter
public enum UserRole {
    MASTER("ROLE_MASTER"),
    HUB_MANAGER("ROLE_HUB_MANAGER"),
    DELIVERY("ROLE_DELIVERY_MANAGER"),
    COMPANY_MANAGER("ROLE_COMPANY_MANAGER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public static UserRole fromString(String role) {
        if (role == null || role.isBlank()) {
            throw new UserException(UserException.UserErrorType.INVALID_ROLE);
        }

        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UserException(UserException.UserErrorType.INVALID_ROLE);
        }
    }

    public boolean isMaster() {
        return this == MASTER;
    }
}
