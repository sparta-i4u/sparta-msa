package com.i4u.product.application.dto.response;

import com.i4u.product.domain.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProductSearchResponse(List<ProductResponse> products, int totalPages,
                                    long totalElements) {

    public static ProductSearchResponse of(Page<Product> productPages) {
        return new ProductSearchResponse(productPages.map(ProductResponse::of).toList(),
                productPages.getTotalPages(), productPages.getTotalElements());
    }
}
