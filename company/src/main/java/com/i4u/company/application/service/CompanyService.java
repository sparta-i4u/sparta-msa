package com.i4u.company.application.service;

import com.i4u.company.application.dto.request.CompanyRequestDto;
import com.i4u.company.application.dto.response.CompanyResponseDto;
import com.i4u.company.domain.Company;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

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

    //업체 검색 service

    @Transactional(readOnly = true)
    public Page<CompanyRequestDto> getCompany(String keyword, int page, int size, String sortBy,
                                           boolean isAsc) {
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Company> companyPage = companyRepository.findByName(keyword, pageable);
        return companyPage.map(CompanyResponseDto::of);
    }

    //업체 수정
    @Transactional
    public CompanyResponseDto updateCompany(UUID companyId, CompanyRequestDto requestDto) {
        Company company = companyRepository.findById(companyId).orElseThrow();

        String name = requestDto.name();
        String address = requestDto.address();
        String number = requestDto.number();

        company.updateName(name);
        company.updateAddress(address);
        company.updateNumber(number);

        Company updatedStore = companyRepository.save(company);
        return CompanyResponseDto.of(updatedStore);
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
