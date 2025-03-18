package com.i4u.product.infrastructure;


import com.i4u.product.application.dto.ProductSearchCond;
import com.i4u.product.domain.Product;
import com.i4u.product.domain.QProduct;
import com.i4u.product.domain.repository.ProductRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@Repository
//@RequiredArgsConstructor
public abstract class ProductRepositoryImpl implements ProductRepository {
//    private final ProductRepository productRepository;
//    private final JPAQueryFactory queryFactory;
//    private final QProduct product = QProduct.product;
//
//    //상품 저장
//    @Override
//    public Product save(Product product) {
//        return productRepository.save(product);
//    }
//
//    //쿼리 DSL 사용하기 위한 코드
//    public ProductRepositoryImpl(JPAQueryFactory queryFactory, ProductRepository productRepository) {
//        this.queryFactory = queryFactory;
//        this.productRepository = productRepository;
//    }
//
//    //쿼리 DSL - 상품 아이디로 Product찾기
//    @Override
//    public Optional<Product> findById(UUID id) {
//        Product result = queryFactory
//                .selectFrom(product)
//                .where(product.id.eq(id))
//                .fetchOne();
//        return Optional.ofNullable(result);
//    }
//
//    //상품 전체 조회 쿼리DSL
//    @Override
//    public Page<Product> findAll(Pageable pageable) {
//        List<Product> products = queryFactory
//                .selectFrom(product)
//                .where(product.deletedAt.isNull()) // deleted_at이 null인 데이터만 조회
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = queryFactory   //전체 데이터 개수
//                .select(product.count())        //전체 개수 조회
//                .from(product)
//                .where(product.deletedAt.isNull()) // deleted_at이 null인 데이터만 조회
//                .fetchFirst();  //fetchOne 대신 fetchFirst 사용 -> null이어도 예외 발생하지 않게
//
//        //products : 현재 페이지 상품 목록, pageable 페이징 정보, total 전체상품개수 - deletedAt null인 상품만
//        return new PageImpl<>(products, pageable, total);
//    }
//
//    //조건에 맞는 상품 조회
//    public Page<Product> search(ProductSearchCond cond, Pageable pageable) {
//        BooleanBuilder builder = getBooleanBuilder(cond);
//        List<Product> products = queryFactory
//                .selectFrom(product)
//                .where(builder)
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        long total = queryFactory
//                .select(product.count())
//                .from(product)
//                .where(builder)
//                .fetchOne();
//        return new PageImpl<>(products, pageable, total);
//    }
//
//    private BooleanBuilder getBooleanBuilder(ProductSearchCond cond) {
//        BooleanBuilder builder = new BooleanBuilder();
//
////        if (cond.hubId() != null) {
////            builder.and(product.hub.id.eq(cond.hubId()));
////        }
////        if (cond.companyId() != null) {
////            builder.and(product.company.id.eq(cond.companyId()));
////       }
//        if (cond.name() != null) {
//            builder.and(product.name.containsIgnoreCase(cond.name()));
//        }
//        if (!cond.isDeleted()) {
//            builder.and(product.isDeleted.eq(false));
//        }
//        return builder;
//    }

//    private List<Product> fetchProducts(ProductSearchCond cond, BooleanBuilder builder,
//                                        Pageable pageable) {
//        return queryFactory.selectFrom(product)  //select
//                .where(builder)        //조건 추가
//                .orderBy(getOrderSpecifiers(pageable))   //정렬
//                .offset(pageable.getOffset())   //페이지 시작 위치
//                .limit(pageable.getPageSize())  //조회할 데이터의 최대 개수
//                .fetch();   //쿼리 실행 후 결과 반환
//    }
//
//    private Long fetchTotalCount(BooleanBuilder builder) {
//        Long total = queryFactory.select(product.count())
//                .from(product)
//                .where(builder)
//                .fetchOne();
//        return total == null ? 0L : total;
//    }
}
