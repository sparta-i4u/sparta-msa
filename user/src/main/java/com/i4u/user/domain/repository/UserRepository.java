package com.i4u.user.domain.repository;

import com.i4u.user.domain.User;
import com.i4u.common.domain.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {

    // 특정 ID의 활성 사용자 조회 (Soft Delete 제외)
    Optional<User> findByUserIdAndIsDeletedFalse(UUID userId); // Long → UUID 변경

    // 특정 Slack ID를 가진 사용자 조회
    Optional<User> findBySlackIdAndIsDeletedFalse(String slackId);

    // 특정 역할(Role)을 가진 활성 사용자 조회
    List<User> findByRoleAndIsDeletedFalse(UserRole role);

    // 페이징 처리를 적용한 모든 활성 사용자 조회 (Soft Delete 제외)
    Page<User> findAllByIsDeletedFalse(Pageable pageable);

    // 특정 역할(Role)을 가진 사용자 페이징 조회 (Soft Delete 제외)
    Page<User> findByRoleAndIsDeletedFalse(UserRole role, Pageable pageable);
}
