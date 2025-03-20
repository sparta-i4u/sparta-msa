package com.i4u.company.domain.repository;

import com.i4u.company.domain.entity.QCompany;
import com.i4u.company.domain.entity.Company;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Page<Company> findAll(Pageable pageable) {
        List<Company> companies = queryFactory
                .selectFrom(company)
                .where(company.isDeleted.isFalse()) // deleted_at이 null인 데이터만 조회
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory   //전체 데이터 개수
                .select(company.count())        //전체 개수 조회
                .from(company)
                .where(company.deletedAt.isNull()) // deleted_at이 null인 데이터만 조회
                .fetchFirst();  //fetchOne 대신 fetchFirst 사용 -> null이어도 예외 발생하지 않게

        return new PageImpl<>(companies, pageable, total);
    }


    //업체 이름 검색 쿼리DSL
    public Page<Company> findByNameContaining(Pageable pageable, String keyword) {
        // 상품 목록을 가져옵니다.
        List<Company> companies = queryFactory
                .selectFrom(company)
                .where(company.name.containsIgnoreCase(keyword)  // 상품 이름에 이름이 포함된 데이터를 조회
                        .and(company.deletedAt.isNull()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();  // 결과 리스트 반환

        // 전체 업체 개수를 가져옵니다.
        long total = queryFactory
                .select(company.count())
                .from(company)
                .where(company.name.containsIgnoreCase(keyword))  // 이름에 포함된 업체 개수 조회
                .fetchOne();  // fetchOne()은 단일 값 반환

        return new PageImpl<>(companies, pageable, total);  // PageImpl로 반환
    }

}