package com.i4u.company.presentation;


import com.i4u.common.utils.CommonResponse;
import com.i4u.company.application.dto.request.CompanyRequestDto;
import com.i4u.company.application.dto.response.CompanyResponseDto;
import com.i4u.company.application.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            final @RequestBody CompanyRequestDto requestDto) {
        CompanyResponseDto response = companyService.createCompany(userDetails,requestDto);
        return new ResponseEntity<>(ResponseVOUtils.getSuccessResponse(response), HttpStatus.CREATED);
    }

    //업체 조회
    //ALL
    @GetMapping("")
    public ResponseEntity<CommonResponse> getCompany(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean isAsc) {
        Page<CompanyResponseDto> response = companyService.getCompany(keyword, page, size, sortBy, isAsc);
        return new ResponseEntity<>(ResponseVOUtils.getSuccessResponse(response), HttpStatus.OK);
    }

    //업체 수정
    //관리 허브 ID가 존재하는 허브인지 확인
    //MASTER, 담당허브, 본인업체
    @PutMapping("/{companyId}")
    public ResponseEntity<CommonResponse> updateCompany(
            @PathVariable UUID companyId, final @RequestBody CompanyRequestDto requestDto) {
        CompanyResponseDto response = companyService.updateCompany(companyId, requestDto);
        return new ResponseEntity<>(ResponseVOUtils.getSuccessResponse(response), HttpStatus.OK);
    }

    //업체 삭제
    //업체 엔티티에 deleted_at, deleted_by 필드를 이용하여 논리적 삭제를 관리합니다.
    //업체가 삭제될 경우 관련된 서비스에서 연관 데이터를 비 활성화할 때 삭제 관련 필드를 기준으로 처리합니다.
    //MASTER, 담당허브
    @DeleteMapping("/{companyId}")
    public ResponseEntity<CommonResponse> deleteCompany(@PathVariable UUID companyId) {
        CompanyResponseDto response = companyService.deleteCompany(companyId);
        return new ResponseEntity<>(ResponseVOUtils.getSuccessResponse(response), HttpStatus.OK);
    }

}
