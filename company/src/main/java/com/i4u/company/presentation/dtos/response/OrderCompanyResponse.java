package com.i4u.company.presentation.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompanyResponse {

    // 요청을 받은/요청을 한 업체를 모두 검증해서 하나라도 없다면 true 값 받아오기
    private Boolean isDeleted;

    // 요청 업체 ID
    private UUID supplierId;

    // 요청 업체가 속한 허브 ID
    private UUID supplierHubId;

    // 수령 업체 ID
    private UUID recipientId;

    // 수령 업체가 속한 허브 ID
    private UUID recipientHubId;

    // 수령 업체의 주소
    private String address;
}
