package com.i4u.product.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductCreateRequest(
        @NotNull(message = "허브아이디는 필수입니다") UUID hubId,
        @NotNull(message = "업체아이디는 필수입니다") UUID companyId,
        @NotBlank(message = "상품 이름은 필수입니다.") String name,
        @NotNull(message = "상품 가격은 필수입니다.") Integer price,
        @NotBlank(message = "상품 설명은 필수입니다.") String content) {
}
