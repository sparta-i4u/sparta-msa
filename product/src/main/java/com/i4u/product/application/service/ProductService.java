package com.i4u.product.application.service;



import com.i4u.product.application.dto.request.ProductCreateRequest;
import com.i4u.product.application.dto.request.ProductUpdateRequest;
import com.i4u.product.application.dto.response.ProductResponse;
import com.i4u.product.application.dto.response.ProductSearchResponse;
import com.i4u.product.domain.Product;
import com.i4u.product.domain.repository.ProductQueryRepository;
import com.i4u.product.domain.repository.ProductRepository;
import com.i4u.product.exception.ProductNotFoundException;
import com.i4u.product.presentation.client.CompanyClient;
import com.i4u.product.presentation.client.HubClient;
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
    private final HubClient hubClient;
    private final CompanyClient companyClient;

    //상품 생성
    @Transactional
    public ProductResponse createProduct(final ProductCreateRequest request, String userId, String role) {
        //TODO 허브아이디와 컴퍼니 아이디 받아오기
        //요청을 보낸 사람(product 외부에서 (client) product로 요청을 보낸 사람- 권한 검증)

        // 허브 id를 - 요청을 할 때 이 업체에다가 상품을 만들거야, 요청 보낸 사용자가 보낸 hubId와 companyId
        final UUID hubId = request.hubId();
        final UUID companyId = request.companyId();

        // 권한 - 요청을 한 사람 - 상품 생성을 한 사용자의 권한 , 즉 요청을 보낸 사용자의 권한
        // 권한 확인 로직 - 본인hub인지 | 본인 company인지
        UUID companyOrHubId = confirmRole(userId, role);

        //해당하는 hub나 company ID가 없으면 - id가 있냐 없느냐만 확인
        if (companyOrHubId == null || role.equals("ROLE_DELIVERY_MANAGER")) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        // 허브 모듈에서 이 로직 만들기
        // hubRepositroy.findById();  - 이거 해달라는 요청
        // 업체 모듈에서 이 로직 만들기
        // companyRepository.findById();

        //권한검증
        //본인 업체가 아니고 and 본인이 담당하는 허브아이디가 아니면 - 정말 본인것이 맞는지 확인하는 로직
        if ( !(role.equals("ROLE_COMPANY_MANAGER") && companyOrHubId.equals(companyId)) ||
             !(role.equals("ROLE_HUB_MANAGER") && companyOrHubId.equals(hubId)) ) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        // 각각 받아온 hub와 company가 존재하는지 확인
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
    public ProductSearchResponse findAll(final int page, final int size, String s, String userId, final String sort) {
        Pageable pageable = getPageable(page, size, sort);
        return ProductSearchResponse.of(productQueryRepository.findAll(pageable));
    }

    //상품 이름 검색 Service
    public ProductSearchResponse findProudctByKeyword(final String keyword, final int page, final int size, String s, String userId, final String sort) {
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
    public ProductResponse updateProduct(final UUID productId, final ProductUpdateRequest request, String userId, String role) {
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
    public void softDeleteProducts(final List<UUID> productIds, String userId, final String deletedBy) {
        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) { // 조회된 상품들이 없으면 예외 처리하거나, 빈 리스트 처리 가능
            throw new ProductNotFoundException("해당하는 상품이 없습니다.");
        }
        // 각 상품에 대해 논리 삭제 처리
        products.forEach(product -> product.softDelete(deletedBy));
    }

    // 권한 확인 SERVICE - ROLE_MASTER, ROLE_HUB_MANAGER(담당 허브), ROLE_COMPANY_MANAGER(본인 업체)
    private UUID confirmRole(String userId, String role) {
        switch (role) {
            case "ROLE_COMPANY_MANAGER":
                // companyClient (companyId - 없으면 null)
                return companyClient.getCompanyId(UUID.fromString(userId));
            case "ROLE_DELIVERY_MANAGER":
                return null;
            case "ROLE_HUB_MANAGER":
                // hubClient (hubId - 없으면 null)
                return hubClient.getHubId(UUID.fromString(userId));  //userId가 이미 UUID 형식의 문자열이라면, 이를 UUID 객체로 바꿔주는 역
            default:  // ROLE_MASTER
                // 걍 통과
                break;
        }
        return null;
    }
}
