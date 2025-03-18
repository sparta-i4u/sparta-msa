package com.i4u.company.infrastructure;


import com.i4u.company.domain.Company;
import com.i4u.company.domain.repository.CompanyJpaRepository;
import com.i4u.company.domain.repository.CompanyRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
public class CompanyRepositoryImpl implements CompanyRepository {

    private final CompanyJpaRepository jpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Company save(Company company) {
        return jpaRepository.save(company);
    }

    @Override
    public Optional<Company> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
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

    @Override
    public void softDeleteCompany(UUID companyId) {
        queryFactory
                .update(company)
                .set(company.isDeleted, true)
                .where(company.id.eq(companyId))
                .execute();

    }
}
