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

    //상품 검색 쿼리DSL
    public Page<Product> findByNameContaining(Pageable pageable, String keyword) {
        // 상품 목록을 가져옵니다.
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(product.name.containsIgnoreCase(keyword)  // 상품 이름에 이름이 포함된 데이터를 조회
                        .and(product.deletedAt.isNull()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();  // 결과 리스트 반환

        // 전체 상품 개수를 가져옵니다.
        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(product.name.containsIgnoreCase(keyword))  // 이름에 포함된 상품 개수 조회
                .fetchOne();  // fetchOne()은 단일 값 반환

        return new PageImpl<>(products, pageable, total);  // PageImpl로 반환
    }
}
