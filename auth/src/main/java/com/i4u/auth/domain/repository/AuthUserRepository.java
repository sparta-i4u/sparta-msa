package com.i4u.auth.domain.repository;

import com.i4u.auth.domain.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    // 이메일로 AuthUser 조회
    Optional<AuthUser> findByEmail(String email);
}