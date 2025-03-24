package com.i4u.product.presentation;


import com.i4u.common.utils.CommonResponse;
import com.i4u.product.application.dto.request.ProductCreateRequest;
import com.i4u.product.application.dto.request.ProductUpdateRequest;
import com.i4u.product.application.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.UUID;

@Tag(name = "상품 API", description = "상품 관련 API")
public interface ProductApi {

    @Operation(summary = "상품 등록", description = "상품을 등록하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "가게 등록 성공",

                    content = @Content(schema = @Schema(implementation = ProductResponse.class)))

    })
    ResponseEntity<CommonResponse> createProduct(ProductCreateRequest request, String userId, String role);


    @Operation(summary = "전체 상품 조회", description = "전체 상품을 조회하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "가게 조회 성공",

                    content = @Content(schema = @Schema(implementation = Page.class)))

    })

    ResponseEntity<CommonResponse> getProducts(int  page, int size, String sort, String userId, String role);


    @Operation(summary = "상품 이름 검색", description = "상품을 이름으로 검색하는 API입니다")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "상품 이름 검색 성공",

                    content = @Content(schema = @Schema(implementation = Page.class)))

    })


    ResponseEntity<CommonResponse> findProudctByKeyword(String Keyword,int page, int size, String sort, String userId, String role);



    @Operation(summary = "상품 수정", description = "특정 상품 정보를 수정하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "업체 수정 성공",

                    content = @Content(schema = @Schema(implementation = ProductResponse.class)))

    })

    ResponseEntity<CommonResponse> updateProduct (UUID productId, ProductUpdateRequest request, String userId, String role);



    @Operation(summary = "상품 삭제", description = "특정 상품을 삭제하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "상품 삭제 성공",

                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))

    })

    ResponseEntity<CommonResponse> softDeleteProducts(List<UUID> productIds, String userId, String role);
}
