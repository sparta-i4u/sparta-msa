package com.i4u.company.application.service;

import com.i4u.company.application.dto.request.CompanyRequestDto;
import com.i4u.company.application.dto.request.CompanyUpdateRequest;
import com.i4u.company.application.dto.response.CompanyResponseDto;
import com.i4u.company.domain.Company;
import com.i4u.company.domain.repository.CompanyQueryRepository;
import com.i4u.company.domain.repository.CompanyRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyQueryRepository companyQueryRepository;;

    //업체 생성 service
    @Transactional
    public CompanyResponseDto createCompany(UserDetailsImpl userDetails, CompanyRequestDto requestDto) {
        /**
         * TODO 중복된 전화번호, 이름 등록불가 , 유저 연관관계 설정
         */
        //User user = userRepository.findById(requestDto.owner()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Company company = new Company(requestDto.hubId(), requestDto.name(), requestDto.type(), requestDto.owner(), requestDto.address(), requestDto.number());
        Company saved = companyRepository.save(company);
        return CompanyResponseDto.of(saved);
    }

    //업체 전체 조회 SERVICE
    @Transactional(readOnly = true)
    public CompanySearchResponse findAll(final int page, final int size, final String sort) {
        Pageable pageable = getPageable(page, size, sort);
        return CompanySearchResponse.of(companyQueryRepository.findAll(pageable));
    }

    //업체 이름 검색 service
//    @Transactional(readOnly = true)
//    public Page<CompanyRequestDto> getCompany(String keyword, int page, int size, String sortBy,
//                                           boolean isAsc) {
//        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
//        Page<Company> companyPage = companyQueryRepository.findByName(keyword, pageable);
//        return companyPage.map(CompanyResponseDto::of);
//    }

    //업체 카테고리 검색 service

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

    //업체 수정
    @Transactional
    public CompanyResponseDto updateCompany(UUID companyId, CompanyUpdateRequest request) {
        final Company company = companyQueryRepository.findById(companyId).orElseThrow();
        company.update(request);
        return CompanyResponseDto.of(company);
    }
    
    //업체 삭제
    @Transactional
    public CompanyResponseDto deleteCompany(UUID companyId) {
        Company store = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company Not FOUND"));
        companyRepository.softDeleteCompany(companyId);
        return CompanyResponseDto.of(store);
    }
}
