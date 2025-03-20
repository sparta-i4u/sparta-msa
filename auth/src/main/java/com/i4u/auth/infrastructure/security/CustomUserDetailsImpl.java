package com.i4u.auth.infrastructure.security;

import com.i4u.auth.domain.AuthUser;
import com.i4u.common.security.CustomUserDetails;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
public class CustomUserDetailsImpl implements CustomUserDetails {

    private final UUID userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetailsImpl(AuthUser authUser) {
        this.userId = authUser.getUserId();
        this.username = authUser.getEmail();
        this.password = authUser.getPassword();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + authUser.getRole().name()));
    }

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
