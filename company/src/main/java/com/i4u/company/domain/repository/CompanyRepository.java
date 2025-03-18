package com.i4u.company.domain.repository;

import com.i4u.company.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {

    Company save(Company company);

    Optional<Company> findById(UUID companyId);

    Page<Company> findByName(String keyword, Pageable pageable);

    void softDeleteCompany(UUID companyId);
}
