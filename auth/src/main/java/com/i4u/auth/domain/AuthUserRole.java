package com.i4u.auth.domain;

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
}
