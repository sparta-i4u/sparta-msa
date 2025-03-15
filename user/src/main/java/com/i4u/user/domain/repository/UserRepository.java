package com.i4u.user.domain.repository;

import com.i4u.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    // 특정 ID의 활성 사용자 조회 (Soft Delete 제외)
    Optional<User> findByUserIdAndIsDeletedFalse(Long userId);

    // 특정 Slack ID를 가진 사용자 조회
    Optional<User> findBySlackId(String slackId);

    // 페이징 처리를 적용한 사용자 검색
    Page<User> findAllByIsDeletedFalse(Pageable pageable);
}