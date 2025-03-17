package com.i4u.product.infrastructure;


import com.i4u.product.application.dto.ProductSearchCond;
import com.i4u.product.domain.Product;
import com.i4u.product.domain.repository.ProductJpaRepository;
import com.i4u.product.domain.repository.ProductRepository;
import com.querydsl.core.BooleanBuilder;
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
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final JPAQueryFactory queryFactory;

    //상품 저장 JPA
    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productJpaRepository.findById(id);
    }

    //상품 조회 JPA
    @Override
    public Page<Product> search(ProductSearchCond cond, Pageable pageable){
        BooleanBuilder builder = getBooleanBuilder(cond);
        List<Product> products = fetchProducts(cond, builder, pageable);
        Long total = fetchTotalCount(builder);
        return new PageImpl<>(products, pageable, total);
    }

    private BooleanBuilder getBooleanBuilder(ProductSearchCond cond) {
        BooleanBuilder builder = new BooleanBuilder();
        QProduct product = QProduct.product;

        //if문에 전부 걸리도록 설정. 해당하는것만 if문 내부 수행
        if (cond.hubId() != null) {
            builder.and(product.hub.id.eq(cond.hubId()));
        }
        if (cond.companyId() != null) {
            builder.and(product.company.id.eq(cond.companyId()));
        }
        if(cond.name() != null) {
            builder.and(product.name.eq(cond.name()));
        }
        if (!cond.isDeleted()) {  //삭제되지 않은 상품만 검색
            builder.and(product.isDeleted.eq(false));
        }
        return builder;
    }

    private List<Product> fetchProducts(ProductSearchCond cond, BooleanBuilder builder,
                                        Pageable pageable) {
        return queryFactory.selectFrom(product)  //select
                .where(builder)        //조건 추가
                .orderBy(getOrderSpecifiers(pageable))   //정렬
                .offset(pageable.getOffset())   //페이지 시작 위치
                .limit(pageable.getPageSize())  //조회할 데이터의 최대 개수
                .fetch();   //쿼리 실행 후 결과 반환
    }

    private Long fetchTotalCount(BooleanBuilder builder) {
        Long total = queryFactory.select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();
        return total == null ? 0L : total;
    }
}
