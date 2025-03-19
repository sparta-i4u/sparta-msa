package com.i4u.company.application.service;

import com.i4u.company.application.dto.request.CompanyCreateRequest;
import com.i4u.company.application.dto.request.CompanyUpdateRequest;
import com.i4u.company.application.dto.response.CompanyResponse;
import com.i4u.company.application.dto.response.CompanySearchResponse;
import com.i4u.company.domain.entity.Company;
import com.i4u.company.domain.repository.CompanyQueryRepository;
import com.i4u.company.domain.repository.CompanyRepository;
import com.i4u.company.exceptiion.CompanyNotFoundException;
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
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyQueryRepository companyQueryRepository;
    ;

    //업체 생성 service
    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request) {
        /**
         * TODO 중복된 전화번호, 이름 체크 / user 로그인 권한 체크
         */
        //User user = userRepository.findById(requestDto.owner()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Company company = new Company(request.hubId(), request.name(), request.type(), request.owner(), request.address(), request.number());
        Company saved = companyRepository.save(company);
        return CompanyResponse.of(saved);
    }

    //업체 전체 조회 SERVICE
    @Transactional(readOnly = true)
    public CompanySearchResponse findAll(final int page, final int size, final String sort) {
        Pageable pageable = getPageable(page, size, sort);
        return CompanySearchResponse.of(companyQueryRepository.findAll(pageable));
    }

    //업체 이름 검색 service
    public CompanySearchResponse findCompanyByKeyword(final String keyword, final int page, final int size, final String sort){
        Pageable pageable = getPageable(page, size, sort);
        // 업체 이름으로 필터링
        if (keyword != null || !keyword.isBlank()) {  //keyword가 있으면
            return CompanySearchResponse.of(companyQueryRepository.findByNameContaining(pageable, keyword));
        }
        //키워드 없으면 원래 Product 조회
        return CompanySearchResponse.of(companyQueryRepository.findAll(pageable));
    }

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
    public CompanyResponse updateCompany(final UUID companyId, final CompanyUpdateRequest request) {
        final Company company = findCompanyById(companyId);
        company.update(request);
        return CompanyResponse.of(company);
    }

    //업체 아이디로 찾기
    public Company findCompanyById(final UUID companyId) {
        return companyQueryRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }


    //업체 삭제
    @Transactional
    public void softDeleteCompanies(final List<UUID> companyIds, final String deletedBy) {
        List<Company> companies = companyRepository.findAllById(companyIds);
        if (companies.isEmpty()) { // 조회된 상품들이 없으면 예외 처리하거나, 빈 리스트 처리 가능
            throw new CompanyNotFoundException(companyIds);
        }
        // 각 상품에 대해 논리 삭제 처리
        companies.forEach(company -> company.softDelete(deletedBy));
    }
}
