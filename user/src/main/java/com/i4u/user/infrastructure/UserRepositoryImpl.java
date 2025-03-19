package com.i4u.user.infrastructure;

import com.i4u.user.domain.QUser;
import com.i4u.user.domain.User;
import com.i4u.user.domain.UserRole;
import com.i4u.user.domain.repository.UserRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<User> searchUsers(String keyword, UserRole role, Pageable pageable, boolean includeDeleted) {
        QUser user = QUser.user;
        BooleanBuilder whereClause = new BooleanBuilder();

        // 키워드 필터 추가 (닉네임, 이메일 검색)
        if (keyword != null && !keyword.isEmpty()) {
            whereClause.or(user.email.containsIgnoreCase(keyword));
            whereClause.or(user.nickname.containsIgnoreCase(keyword));
        }

        // 특정 역할(Role) 필터 추가
        if (role != null) {
            whereClause.and(user.role.eq(role));
        }

        // 논리 삭제 필터 추가
        if (!includeDeleted) {
            whereClause.and(user.isDeleted.eq(false));
        }

        List<User> users = queryFactory
                .selectFrom(user)
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(user)
                .where(whereClause)
                .fetchCount();

        return new PageImpl<>(users, pageable, total);
    }
}
