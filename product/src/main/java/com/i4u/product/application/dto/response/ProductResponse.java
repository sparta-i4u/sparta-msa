package com.i4u.product.application.dto.response;


import com.i4u.product.domain.Product;

import java.util.UUID;

public record ProductResponse (
        UUID productId,
        UUID companyId,
        UUID HubId,
        String name,
        Integer price,
        String content) {

    public static ProductResponse of(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCompanyId(),
                product.getHubId(),
                product.getName(),
                product.getPrice(),
                product.getContent()
        );
    }

    // Getter 메서드들
    public UUID getId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }
}