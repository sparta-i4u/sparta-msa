package com.i4u.product.domain.repository;

import com.i4u.product.application.dto.ProductSearchCond;
import com.i4u.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save (Product product);

    Page<Product> search(ProductSearchCond cond, Pageable pageable);

    Optional<Product> findById(UUID id);

    //상품 전체조회
    Page<Product> findAll(Pageable pageable);
}
