package com.i4u.company.application.service;

import com.i4u.company.application.dto.request.CompanyCreateRequest;
import com.i4u.company.application.dto.request.CompanyUpdateRequest;
import com.i4u.company.application.dto.response.CompanyResponse;
import com.i4u.company.application.dto.response.CompanySearchResponse;
import com.i4u.company.domain.entity.Company;
import com.i4u.company.domain.repository.CompanyQueryRepository;
import com.i4u.company.domain.repository.CompanyRepository;
import com.i4u.company.exception.CompanyNotFoundException;
import com.i4u.company.presentation.client.AuthClient;
import com.i4u.company.presentation.client.HubClient;
import com.i4u.company.presentation.dtos.response.ConfirmUserResponse;
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
    private final HubClient hubClient;
    private final AuthClient authClient;

    // 업체 생성 service
    // ROLE_MASTER, ROLE_HUB_MANAGER(본인이 관리하는 허브만 추가 가능)
    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request, String userId, String role) {
        // 1. [hubClient] 로 요청
        // 지금 업체 생성을 요청한 사용자가 허브 담당자라면,
        // 이 허브 담당자가 관리하는 허브의 ID 를 받아오는 과정  - 허브가 본인이 관리하는 허브인지 이미 검증
        if (role.equals("ROLE_HUB_MANAGER")) {
            UUID responseHub = hubClient.getHubInfo(UUID.fromString(userId));
            if (! responseHub.equals(request.hubId())) {
                throw new CompanyNotFoundException(request.hubId());
            }
        }

        // 2. [authClient] 실제 owner가 업체 담당자가 맞는지 검증 필요 - authClient
        ConfirmUserResponse responseUser = authClient.confirmUser(request.owner());
        if (!responseUser.getUserRole().equals("COMPANY_MANAGER")) {
            throw new IllegalArgumentException("권한이 없습니다. ");
        }

        Company company = new Company(request.hubId(), request.name(), request.type(), request.owner(), request.address(), request.number());
        Company saved = companyRepository.save(company);
        return CompanyResponse.of(saved);
    }

    //업체 전체 조회 SERVICE
    @Transactional(readOnly = true)
    public CompanySearchResponse findAll(final int page, final int size, final String sort, String userId, String role) {


        Pageable pageable = getPageable(page, size, sort);
        return CompanySearchResponse.of(companyQueryRepository.findAll(pageable));
    }

    //업체 이름 검색 service
    public CompanySearchResponse findCompanyByKeyword(final String keyword, final int page, final int size, final String sort, String userId, String role){
        Pageable pageable = getPageable(page, size, sort);
        // 업체 이름으로 필터링
        if (keyword != null || !keyword.isBlank()) {  //keyword가 있으면
            return CompanySearchResponse.of(companyQueryRepository.findByNameContaining(pageable, keyword));
        }
        //키워드 없으면 원래 Product 조회
        return CompanySearchResponse.of(companyQueryRepository.findAll(pageable));
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

    //업체 수정
    // MASTER, 담당허브, 본인업체
    @Transactional
    public CompanyResponse updateCompany(final UUID companyId, final CompanyUpdateRequest request, String userId, String role) {
        final Company company = findCompanyById(companyId);

        UUID userOrHubId = confirmRole(userId, role);

        if (role.equals("ROLE_COMPANY_MANAGER") && !userOrHubId.equals(companyId)) {
            throw new CompanyNotFoundException(companyId);
        } else if (role.equals("ROLE_HUB_MANAGER") && userOrHubId.equals(company.getHubId())) {
            throw new CompanyNotFoundException(companyId);
        } else if (!role.equals("ROLE_MASTER")) {
            throw new IllegalArgumentException("권한이 없습니다");
        }

        // 권한 검증이 끝났으니 update
        company.update(request);
        return CompanyResponse.of(company);
    }

    //업체 아이디로 찾기
    public Company findCompanyById(final UUID companyId) {
        return companyQueryRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    //업체 삭제
    //MASTER, 담당허브
    @Transactional
    public void softDeleteCompanies(final List<UUID> companyIds, String userId, String role) {

        UUID hubId = confirmRole(userId, role);
        if (!role.equals("MASTER") && !role.equals("HUB_MANAGER")) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        List<Company> companies = companyRepository.findAllById(companyIds);

        //권한이 허브매니저라면
        if ( role.equals("ROLE_HUB_MANAGER") ) {
            for (Company c: companies) {   // 삭제할 상품들 하나씩 꺼냄
                if (! hubId.equals(c.getHubId())) {
                    // hubId와 일치하는 경우만 하도록, 불일치 할 경우 리스트에서 제거
                    // hubId와 일치하는 리스트만 내려가도록
                    companies.remove(c);
                }
            }
        }

        if (companies.isEmpty()) { // 조회된 회사가 없으면 예외 처리하거나, 빈 리스트 처리 가능
            throw new CompanyNotFoundException(companyIds);
        }
        // 각 상품에 대해 논리 삭제 처리
        companies.forEach(company -> company.softDelete(userId));
    }

    // 권한 확인 SERVICE - ROLE_MASTER, ROLE_HUB_MANAGER(담당 허브), ROLE_COMPANY_MANAGER(본인 업체)
    private UUID confirmRole(String userId, String role) {
        switch (role) {
            case "ROLE_COMPANY_MANAGER":
                // companyClient (companyId - 없으면 null)
                Company company = companyRepository.findByOwner(UUID.fromString(userId)).orElseThrow(
                        () -> new IllegalArgumentException("message") );
                return company.getId();  //요청하는 사람이 담당하고 있는 id를 들고온다.
            case "ROLE_DELIVERY_MANAGER":
                return null;
            case "ROLE_HUB_MANAGER":
                return hubClient.getHubInfo(UUID.fromString(userId)); //userId가 이미 UUID 형식의 문자열이라면, 이를 UUID 객체로 바꿔주는 역
            default:  // ROLE_MASTER
                // 걍 통과
                break;
        }
        return null;
    }
}