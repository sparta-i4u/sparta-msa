package com.i4u.auth.domain.repository;

import com.i4u.auth.domain.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    // ✅ 이메일 기반으로 사용자 조회 (논리 삭제된 계정 제외)
    Optional<AuthUser> findByEmailAndIsDeletedFalse(String email);

    // ✅ userId 기반으로 사용자 조회 (논리 삭제된 계정 제외)
    Optional<AuthUser> findByUserIdAndIsDeletedFalse(UUID userId);
}
