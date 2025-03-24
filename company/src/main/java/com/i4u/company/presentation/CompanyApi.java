package com.i4u.company.presentation;


import com.i4u.common.utils.CommonResponse;
import com.i4u.company.application.dto.request.CompanyCreateRequest;
import com.i4u.company.application.dto.request.CompanyUpdateRequest;
import com.i4u.company.application.dto.response.CompanyResponse;
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

@Tag(name = "업체 API", description = "업체 관련 API")
public interface CompanyApi {

    @Operation(summary = "업체 등록", description = "가게를 등록하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "가게 등록 성공",

                    content = @Content(schema = @Schema(implementation = CompanyResponse.class)))

    })
    ResponseEntity<CommonResponse>  createCompany(CompanyCreateRequest request, String userId, String role);


    @Operation(summary = "전체 업체 조회", description = "전체 업체를 조회하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "가게 조회 성공",

                    content = @Content(schema = @Schema(implementation = Page.class)))

    })

    ResponseEntity<CommonResponse> getCompany(int  page, int size, String sort, String userId, String role);


    @Operation(summary = "업체 이름 검색", description = "업체를 이름으로 검색하는 API입니다")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "가게 이름 검색 성공",

                    content = @Content(schema = @Schema(implementation = Page.class)))

    })

    ResponseEntity<CommonResponse> findCompanyByKeyword(String Keyword,int page, int size, String sort, String userId, String role);



    @Operation(summary = "업체 수정", description = "특정 업체 정보를 수정하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "업체 수정 성공",

                    content = @Content(schema = @Schema(implementation = CompanyResponse.class)))

    })

    ResponseEntity<CommonResponse> updateCompany (UUID companyId, CompanyUpdateRequest request, String userId, String role);



    @Operation(summary = "업체 삭제", description = "특정 업체를 삭제하는 API입니다.")

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "가게 삭제 성공",

                    content = @Content(schema = @Schema(implementation = CommonResponse.class)))

    })

    ResponseEntity<CommonResponse> softDeleteCompanies(List<UUID> companyIds, String userId, String role);
}
