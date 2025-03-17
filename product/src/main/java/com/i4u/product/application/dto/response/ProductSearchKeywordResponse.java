package com.i4u.product.application.dto.response;

import com.i4u.product.domain.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductSearchKeywordResponse(List<Product> productCategories,
                                          int totalPages,
                                          long totalElements) {

    public static ProductSearchKeywordResponse of(Page<Product> productPages) {
        return new ProductSearchKeywordResponse(
                productPages.map(ProductResponse::of).toList(),
                productPages.getTotalPages(), productPages.getTotalElements());
    }
}
