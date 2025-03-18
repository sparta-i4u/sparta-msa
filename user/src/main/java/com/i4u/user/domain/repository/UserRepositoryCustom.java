package com.i4u.user.domain.repository;

import com.i4u.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserRepositoryCustom {
    Page<User> searchUsers(String keyword, Pageable pageable); // 검색 시 페이징 지원
    Optional<User> findBySlackId(String slackId); // Slack ID로 사용자 조회
}
