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
public class OrderCompanyUpdateResponse {

    private Boolean isDeleted;
    private UUID supplierId;
    private UUID supplierHubId;

}