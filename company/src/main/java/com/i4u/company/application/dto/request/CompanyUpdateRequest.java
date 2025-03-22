package com.i4u.company.application.dto.request;

import com.i4u.company.domain.entity.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CompanyUpdateRequest (
        @NotBlank(message = "업체 이름은 필수입니다.") String name,
        @NotNull(message = "업체 타입은 필수입니다.") CompanyType type,
        @NotBlank(message = "업체 주소는 필수입니다.") String address,
        @NotBlank(message = "업체 번호는 필수입니다.") String number) {
}