package com.i4u.product.domain.repository;


import com.i4u.product.application.dto.ProductSearchCond;
import com.i4u.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
}
