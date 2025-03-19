package com.i4u.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.UUID;

// UserDetails 인터페이스를 확장하여 UUID 기반의 사용자 식별자를 제공하는 인터페이스
public interface CustomUserDetails extends UserDetails {
    UUID getUserId();
}
