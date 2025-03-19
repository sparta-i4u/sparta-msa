package com.i4u.product.domain.repository;

import com.i4u.product.application.dto.ProductSearchCond;
import com.i4u.product.domain.Product;
import com.i4u.product.domain.QProduct;
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
public class ProductQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;

    //쿼리 DSL - 상품 아이디로 Product 찾기
    public Optional<Product> findById(UUID id) {
        Product result = queryFactory
                .selectFrom(product)
                .where(product.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    //상품 전체 조회 쿼리DSL
    public Page<Product> findAll(Pageable pageable) {
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(product.isDeleted.isFalse()) // deleted_at이 null인 데이터만 조회
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory   //전체 데이터 개수
                .select(product.count())        //전체 개수 조회
                .from(product)
                .where(product.deletedAt.isNull()) // deleted_at이 null인 데이터만 조회
                .fetchFirst();  //fetchOne 대신 fetchFirst 사용 -> null이어도 예외 발생하지 않게

        //products : 현재 페이지 상품 목록, pageable 페이징 정보, total 전체상품개수 - deletedAt null인 상품만
        return new PageImpl<>(products, pageable, total);
    }

    //조건에 맞는 상품 조회
    public Page<Product> search(ProductSearchCond cond, Pageable pageable) {
        BooleanBuilder builder = getBooleanBuilder(cond);
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();
        return new PageImpl<>(products, pageable, total);
    }

    private BooleanBuilder getBooleanBuilder(ProductSearchCond cond) {
        BooleanBuilder builder = new BooleanBuilder();

//        if (cond.hubId() != null) {
//            builder.and(product.hub.id.eq(cond.hubId()));
//        }
//        if (cond.companyId() != null) {
//            builder.and(product.company.id.eq(cond.companyId()));
//       }
        if (cond.name() != null) {
            builder.and(product.name.containsIgnoreCase(cond.name()));
        }
        if (!cond.isDeleted()) {
            builder.and(product.isDeleted.eq(false));
        }
        return builder;
    }
}
