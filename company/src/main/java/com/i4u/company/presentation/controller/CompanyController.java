package com.i4u.company.presentation.controller;

import com.i4u.common.utils.CommonResponse;
import com.i4u.company.application.dto.request.CompanyCreateRequest;
import com.i4u.company.application.dto.request.CompanyUpdateRequest;
import com.i4u.company.application.dto.response.CompanyResponse;
import com.i4u.company.application.dto.response.CompanySearchResponse;
import com.i4u.company.application.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/companies")
@RestController
@RequiredArgsConstructor
public class CompanyController {
    //hubId, user 정보 필요
    //모든 업체는 특정 허브에 소속되어 있다.

    private final CompanyService companyService;

    //업체 생성
    //관리 허브 ID가 존재하는 허브인지 확인
    //MASTER, 담당 허브 관리자
    @PostMapping("")
    public ResponseEntity<CommonResponse> createCompany(
            @Valid @RequestBody final CompanyCreateRequest request,
            @RequestHeader(name = "X-User-Id") String userId,
            @RequestHeader(name = "X-User-Role") String role) {
        CompanyResponse response = companyService.createCompany(request, userId, role);
        return new ResponseEntity<>(CommonResponse.success(response, "상품 등록이 정상 수행되었습니다"), HttpStatus.CREATED);
    }

    //업체 전체 조회
    //ALL
    @GetMapping("/search")
    public ResponseEntity<CommonResponse> getCompany (
            @RequestParam final int page,
            @RequestParam final int size,
            @RequestParam(required = false) final String sort,
            @RequestHeader(name = "X-User-Id") String userId,
            @RequestHeader(name = "X-User-Role") String role) {
        CompanySearchResponse response = companyService.findAll(page, size, sort, userId, role);
        return new ResponseEntity<>(CommonResponse.success(response, "업체 목록이 정상 조회되었습니다"), HttpStatus.OK);
    }

    //업체 이름으로 검색
    @GetMapping("/search/keyword")
    public ResponseEntity<CommonResponse> findCompanyByKeyword(
            @RequestParam final String keyword,
            @RequestParam final int page,
            @RequestParam final int size,
            @RequestParam(required = false) final String sort,
            @RequestHeader(name = "X-User-Id") String userId,
            @RequestHeader(name = "X-User-Role") String role) {
        CompanySearchResponse response = companyService.findCompanyByKeyword(keyword, page, size, sort, userId, role);
        return new ResponseEntity<>(CommonResponse.success(response, "업체 키워드로 검색하였습니다"), HttpStatus.OK);
    }

    //업체 수정
    //관리 허브 ID가 존재하는 허브인지 확인
    //MASTER, 담당허브, 본인업체
    @PutMapping("/{companyId}")
    public ResponseEntity<CommonResponse> updateCompany(
            @PathVariable UUID companyId,
            @Valid @RequestBody final CompanyUpdateRequest request,
            @RequestHeader(name = "X-User-Id") String userId,
            @RequestHeader(name = "X-User-Role") String role) {
        CompanyResponse response = companyService.updateCompany(companyId, request, userId, role);
        return new ResponseEntity<>(CommonResponse.success(response, "업체 정보가 수정되었습니다"), HttpStatus.OK);
    }

    //업체 삭제
    //업체 엔티티에 deleted_at, deleted_by 필드를 이용하여 논리적 삭제를 관리합니다.
    //업체가 삭제될 경우 관련된 서비스에서 연관 데이터를 비 활성화할 때 삭제 관련 필드를 기준으로 처리합니다.
    //MASTER, 담당허브
    @DeleteMapping("")
    public ResponseEntity<CommonResponse> softDeleteCompanies(
            @RequestBody final List<UUID> companyIds,
            @RequestHeader(name = "X-User-Id") String userId,
            @RequestHeader(name = "X-User-Role") String role) {
        companyService.softDeleteCompanies(companyIds, userId, role);
        return new ResponseEntity<>(CommonResponse.success("", "상품이 정상적으로 삭제되었습니다"), HttpStatus.OK);
    }
}