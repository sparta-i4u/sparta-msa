package com.i4u.product.application.service;



import com.i4u.product.application.dto.request.ProductCreateRequest;
import com.i4u.product.application.dto.request.ProductUpdateRequest;
import com.i4u.product.application.dto.response.ProductResponse;
import com.i4u.product.application.dto.response.ProductSearchResponse;
import com.i4u.product.domain.Product;
import com.i4u.product.domain.repository.ProductQueryRepository;
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

    private final ProductQueryRepository productQueryRepository;  // QueryDSL 용도
    private final ProductRepository productRepository;  // CRUD 용도

    //상품 생성
    @Transactional
    public ProductResponse createProduct(final ProductCreateRequest request){

        //TODO 허브아이디와 컴퍼니 아이디 받아오기
        final UUID hubId = request.hubId();
        final UUID companyId = request.companyId();

        //TODO 상품 관리 허브 id 확인해 존재하는지 확인
        //        if (!hubRepository.existsById(hubId)) {
        //            throw new IllegalArgumentException("허브가 존재하지 않습니다: " + hubId);
        //        }
        //TODO 상품 company가 존재하는지 확인
        //        if (!companyRepository.existsById(companyId)) {
        //            throw new IllegalArgumentException("업체가 존재하지 않습니다: " + companyId);
        //        }

        final Product product = new Product(hubId, companyId, request.name(), request.price(), request.content(), request.count());
        Product saved = productRepository.save(product);
        return ProductResponse.of(saved);
    }

    //모든 상품 전체 조회
    //모든 조회 및 검색에서 deleted_at 필드가 null인 데이터만을 대상으로 처리
    public ProductSearchResponse findAll(final int page, final int size, final String sort) {
        Pageable pageable = getPageable(page, size, sort);
        return ProductSearchResponse.of(productQueryRepository.findAll(pageable));
    }

    //상품 이름 검색 Service
    public ProductSearchResponse findProudctByKeyword(final String keyword, final int page, final int size, final String sort){
        Pageable pageable = getPageable(page, size, sort);
        // 상품 이름으로 필터링
        if (keyword != null || !keyword.isBlank()) {  //keyword가 있으면
            return ProductSearchResponse.of(productQueryRepository.findByNameContaining(pageable, keyword));
        }
        //키워드 없으면 원래 Product 조회
        return ProductSearchResponse.of(productQueryRepository.findAll(pageable));
    }

    //페이징 함수
    private Pageable getPageable(final int page, final int size, final String sort) {

        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size); // 기본 정렬 없음
        }

        String[] sortParams = sort.split(",");
        List<Sort.Order> orders = new ArrayList<>();

        for (String param : sortParams) {
            String[] fieldAndDirection = param.trim().split("[- ]"); // '-' 또는 ' '으로 구분
            if (fieldAndDirection.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid sort parameter format. Expected 'field direction' (e.g., 'name asc').");
            }

            String field = fieldAndDirection[0].trim();
            String direction = fieldAndDirection[1].trim().toUpperCase();

            if (!direction.equals("ASC") && !direction.equals("DESC")) {
                throw new IllegalArgumentException("Invalid sort direction. Use 'asc' or 'desc'.");
            }

            Sort.Direction dir = Sort.Direction.fromString(direction);
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
        return productQueryRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("찾는 상품이 없습니다"));
    }

    //상품 삭제
    //상품 엔티티의 deleted_at, deleted_by 필드를 이용하여 논리적 삭제를 관리합니다.
    //상품이 삭제될 때 연관된 데이터(주문 등)도 삭제 관련 필드를 통해 관리합니다.
    @Transactional
    public void softDeleteProducts(final List<UUID> productIds, final String deletedBy){
        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) { // 조회된 상품들이 없으면 예외 처리하거나, 빈 리스트 처리 가능
            throw new ProductNotFoundException("해당하는 상품이 없습니다.");
        }
        // 각 상품에 대해 논리 삭제 처리
        products.forEach(product -> product.softDelete(deletedBy));
    }
}
