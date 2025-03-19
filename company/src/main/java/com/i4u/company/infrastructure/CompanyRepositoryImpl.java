package com.i4u.company.infrastructure;


import com.i4u.company.domain.Company;
import com.i4u.company.domain.repository.CompanyJpaRepository;
import com.i4u.company.domain.repository.CompanyRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class CompanyRepositoryImpl implements CompanyRepository {

}
