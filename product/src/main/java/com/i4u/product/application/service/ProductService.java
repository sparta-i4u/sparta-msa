package com.i4u.product.application.service;


import com.i4u.product.application.dto.ProductSearchCond;
import com.i4u.product.application.dto.request.ProductCreateRequest;
import com.i4u.product.application.dto.request.ProductUpdateRequest;
import com.i4u.product.application.dto.response.ProductResponse;
import com.i4u.product.application.dto.response.ProductSearchResponse;
import com.i4u.product.domain.Product;
import com.i4u.product.domain.repository.ProductRepository;
import com.i4u.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final HubRepository hubRepository;
    private final CompanyRepository companyRepository;

    //상품 생성
    @Transactional
    public ProductResponse createProduct(final ProductCreateRequest request){
        //허브아이디와 컴퍼니 아이디 받아오기
        final UUID hubId = request.hubId();
        final UUID companyId = request.companyId();

        //상품 관리 허브 id 확인해 존재하는지 확인
        if (!hubRepository.existsById(hubId)) {
            throw new IllegalArgumentException("허브가 존재하지 않습니다: " + hubId);
        }

        //상품 company가 존재하는지 확인
        if (!companyRepository.existsById(companyId)) {
            throw new IllegalArgumentException("업체가 존재하지 않습니다: " + companyId);
        }
        final Product product = new Product(hubId, companyId, request.name(), request.price(), request.content());
        Product saved = productRepository.save(product);
        return ProductResponse.of(saved);
    }

    //상품 목록 조회 - 필터링
    //모든 조회 및 검색에서 deleted_at 필드가 null인 데이터만을 대상으로 처리
    public ProductSearchResponse search(final ProductSearchCond cond, final int page, final int size, final String sort) {
        Pageable pageable = getPageable(page, size, sort);
        return ProductSearchResponse.of(productRepository.search(cond, pageable));
    }

    //모든 상품 전체 조회
    //모든 조회 및 검색에서 deleted_at 필드가 null인 데이터만을 대상으로 처리
    public ProductSearchResponse findAll(final int page, final int size, final String sort) {
        Pageable pageable = getPageable(page, size, sort);
        return ProductSearchResponse.of(productRepository.findAll(pageable));
    }

    //페이징 함수
    private Pageable getPageable(final int page, final int size, final String sort) {
        String[] sortParams = sort.split(",");
        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            String[] fieldAndDirection = param.trim().split("-");
            if (fieldAndDirection.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid sort parameter format. Expected 'field direction'.");
            }
            String field = fieldAndDirection[0];
            String direction = fieldAndDirection[1].toUpperCase();
            Sort.Direction dir = direction.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            orders.add(new Sort.Order(dir, field));
        }
        Sort sortObj = Sort.by(orders);
        return PageRequest.of(page, size, sortObj);
    }

    //상품 수정
    @Transactional
    public ProductResponse updateProduct(final UUID productId, final ProductUpdateRequest request) {
        final Product product = findProductById(productId);
        product.update(request);
        return ProductResponse.of(product);
    }

    //상품 아이디로 찾기
    public Product findProductById(final UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("찾는 상품이 없습니다"));
    }

    //상품 삭제
    //상품 엔티티의 deleted_at, deleted_by 필드를 이용하여 논리적 삭제를 관리합니다.
    // 상품이 삭제될 때 연관된 데이터(주문 등)도 삭제 관련 필드를 통해 관리합니다.
    @Transactional
    public void softDeleteProducts(final List<UUID> productIds){
        productIds.stream().map(this::findProductById).forEach(Product::softDelete);
    }

    //스트림으로 변환해 데이터 처리
    //findProductById 호출해 UUID-> Product로 변환
    //.forEach 는 반복문 같은 역할, 하나씩 뽑아서
    // product1.softDelete();
    //product2.softDelete();
    //product3.softDelete();  ... 방식으로 반복문수행
}
