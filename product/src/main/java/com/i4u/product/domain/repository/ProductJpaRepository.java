package com.i4u.product.domain.repository;


import com.i4u.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
}
