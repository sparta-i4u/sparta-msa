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
    //허브랑 user필요

    private final CompanyService companyService;

    //업체 생성
    //@Secured
    @PostMapping("")
    public ResponseEntity<CommonResponse> createCompany(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            final @RequestBody CompanyRequestDto requestDto) {
        CompanyResponseDto response = companyService.createCompany(userDetails,requestDto);
        return new ResponseEntity<>(ResponseVOUtils.getSuccessResponse(response), HttpStatus.CREATED);
    }

    //업체 조회
    //@Secured
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
    //@Secured({UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.OWNER})
    @PutMapping("/{companyId}")
    public ResponseEntity<CommonResponse> updateCompany(
            @PathVariable UUID companyId, final @RequestBody CompanyRequestDto requestDto) {
        CompanyResponseDto response = companyService.updateCompany(companyId, requestDto);
        return new ResponseEntity<>(ResponseVOUtils.getSuccessResponse(response), HttpStatus.OK);
    }

    //업체 삭제
   // @Secured({UserRoleEnum.Authority.MANAGER, UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.OWNER})
    @DeleteMapping("/{companyId}")
    public ResponseEntity<CommonResponse> deleteCompany(@PathVariable UUID companyId) {
        CompanyResponseDto response = companyService.deleteCompany(companyId);
        return new ResponseEntity<>(ResponseVOUtils.getSuccessResponse(response), HttpStatus.OK);
    }

}
