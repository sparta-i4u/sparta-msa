package com.i4u.product.presentation;


import com.i4u.product.application.dto.request.ProductCreateRequest;
import com.i4u.product.application.dto.request.ProductUpdateRequest;
import com.i4u.product.application.dto.response.ProductResponse;
import com.i4u.product.application.dto.response.ProductSearchResponse;
import com.i4u.product.application.service.ProductService;
import com.i4u.common.utils.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    //상품 등록
    //담당허브와 본인업체만 가능하게
    //@Secured({Authority.ROLE_COMPANY_MANAGER, Authority.ROLE_HUB_MANAGER, Authority.ROLE_MASTER})
    @PostMapping("")
    public ResponseEntity<CommonResponse> createProduct(
            @Valid @RequestBody final ProductCreateRequest request){
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(CommonResponse.success(response, "상품 등록이 정상 수행되었습니다"),
                HttpStatus.CREATED);
    }

    //상품 목록 전체 조회 - 누구나 다 조회 가능
    //@Secured({Authority.ROLE_DELIVERY_MANAGER, Authority.ROLE_COMPANY_MANAGER, Authority.ROLE_HUB_MANAGER, Authority.ROLE_MASTER})
    @GetMapping("/search")
    public ResponseEntity<CommonResponse> getProducts(
            @RequestParam final int page,
            @RequestParam final int size,
            @RequestParam(required = false) final String sort) {
        ProductSearchResponse response = productService.findAll(page, size, sort);
        return new ResponseEntity<>(CommonResponse.success(response, "상품 목록이 정상 조회되었습니다"), HttpStatus.OK);
    }

    //상품이름으로 검색 - 키워드로 검색 기능
    //누구나 다 검색 가능
    //@Secured({Authority.ROLE_DELIVERY_MANAGER, Authority.ROLE_COMPANY_MANAGER, Authority.ROLE_HUB_MANAGER, Authority.ROLE_MASTER})
    @GetMapping("/search/keyword")
    public ResponseEntity<CommonResponse> findProudctByKeyword(
            @RequestParam final String keyword,
            @RequestParam final int page,
            @RequestParam final int size,
            @RequestParam(required = false) final String sort) {
        ProductSearchResponse response = productService.findProudctByKeyword(keyword, page, size, sort);
        return new ResponseEntity<>(CommonResponse.success(response, "상품 키워드로 검색하였습니다"), HttpStatus.OK);
    }

    //상품 전체 정보 수정
    //본인 업체와 담당 허브만
    //@Secured({Authority.ROLE_COMPANY_MANAGER, Authority.ROLE_HUB_MANAGER, Authority.ROLE_MASTER})
    @PutMapping("/{productId}")
    public ResponseEntity<CommonResponse> updateProduct(
            @PathVariable final UUID productId,
            @Valid @RequestBody final ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return new ResponseEntity<>(CommonResponse.success(response, "상품 정보가 수정되었습니다"), HttpStatus.OK);
    }

    //상품 삭제 - 여러상품도 가능
    //담당허브만 가능
    //API 요청시 [] 리스트형태로 전송
    //@Secured({Authority.ROLE_HUB_MANAGER, Authority.ROLE_MASTER})
    @DeleteMapping("")
    public ResponseEntity<CommonResponse> softDeleteProducts(
            @RequestBody final List<UUID> productIds){
        productService.softDeleteProducts(productIds);
        return new ResponseEntity<>(CommonResponse.success("", "상품이 정상적으로 삭제되었습니다"), HttpStatus.OK);
    }
}