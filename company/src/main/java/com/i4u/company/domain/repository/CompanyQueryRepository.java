package com.i4u.company.domain.repository;

import com.i4u.company.domain.Company;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.i4u.product.domain.QCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CompanyQueryRepository {

    //private final CompanyJpaRepository jpaRepository;
    private final JPAQueryFactory queryFactory;
    private final QCompany company = QCompany.company;

    //업체 아이디로 찾기
    public Optional<Company> findById(UUID id) {
        Company result = queryFactory
                .selectFrom(company)
                .where(company.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    //업체 전체 조회 쿼리DSL


    //업체 이름으로 찾기
    public Page<Company> findByName(String keyword, Pageable pageable) {
        List<Company> companies = queryFactory
                .selectFrom(company)
                .where(
                        company.name.value.containsIgnoreCase(keyword), // 대소문자 무시
                        company.isDeleted.eq(false) // 삭제되지 않은 가게만 조회
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(company.count())
                .from(company)
                .where(
                        company.name.value.containsIgnoreCase(keyword),
                        company.isDeleted.eq(false) // 삭제되지 않은 가게만 조회
                )
                .fetchOne();
        return new PageImpl<>(companies, pageable, total != null ? total : 0);
    }

}
