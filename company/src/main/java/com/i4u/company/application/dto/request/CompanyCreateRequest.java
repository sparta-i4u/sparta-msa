package com.i4u.company.application.dto.request;

import com.i4u.company.domain.entity.CompanyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CompanyCreateRequest(
        @NotNull(message = "허브아이디는 필수입니다") UUID hubId,
        @NotBlank(message = "업체이름은 필수입니다.") String name,
        @NotNull(message = "업체 타입은 필수입니다.") CompanyType type,
        @NotNull(message = "업체소유주 등록은 필수입니다.") UUID owner,
        @NotBlank(message = "업체주소는 필수입니다.") String address,
        @NotBlank(message = "업체번호는 필수입니다.") String number
) {

}