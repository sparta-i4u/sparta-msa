package com.i4u.user.infrastructure;

import com.i4u.common.config.QueryDslConfig;
import com.i4u.user.domain.QUser;
import com.i4u.user.domain.User;
import com.i4u.user.domain.repository.UserRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        QUser user = QUser.user;

        // 검색 조건 (username, nickname, email 중 하나라도 포함)
        BooleanExpression condition = user.isDeleted.eq(false)
                .and(user.username.containsIgnoreCase(keyword)
                        .or(user.nickname.containsIgnoreCase(keyword))
                        .or(user.email.containsIgnoreCase(keyword)));

        // 전체 개수 조회 (페이징 계산을 위해 필요)
        long totalCount = queryFactory.selectFrom(user)
                .where(condition)
                .fetchCount();

        // 페이징 처리된 결과 조회
        List<User> results = queryFactory.selectFrom(user)
                .where(condition)
                .offset(pageable.getOffset()) // 시작 위치
                .limit(pageable.getPageSize()) // 한 페이지에 포함할 개수
                .fetch();

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Optional<User> findBySlackId(String slackId) {
        QUser user = QUser.user;

        return Optional.ofNullable(
                queryFactory.selectFrom(user)
                        .where(user.slackId.eq(slackId).and(user.isDeleted.eq(false)))
                        .fetchOne()
        );
    }
}
