package com.i4u.product.domain.repository;

import com.i4u.product.application.dto.ProductSearchCond;
import com.i4u.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.jaas.JaasPasswordCallbackHandler;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
//
//    Product save (Product product);
//
//    Page<Product> search(ProductSearchCond cond, Pageable pageable);
//
//    Optional<Product> findById(UUID id);
//
//    //상품 전체조회
//    Page<Product> findAll(Pageable pageable);
}
