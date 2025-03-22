package com.i4u.hub.domain.repository;

import com.i4u.hub.domain.model.Hub;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.i4u.hub.domain.model.QHub.hub;

@RequiredArgsConstructor
public class HubCustomRepositoryImpl implements HubCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Hub> findAllWithPagination(Pageable pageable) {
        List<Hub> content = queryFactory
                .selectFrom(hub)
                .where(hub.deletedAt.isNull()) // 삭제되지 않은 허브만 조회
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(hub.createdAt.desc()) // 생성일 기준 내림차순 정렬
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(hub.count())
                .from(hub)
                .where(hub.deletedAt.isNull());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
