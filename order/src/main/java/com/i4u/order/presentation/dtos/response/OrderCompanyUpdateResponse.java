package com.i4u.order.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCompanyUpdateResponse {

	private Boolean isDeleted;
	private UUID supplierId;
	private UUID supplierHubId;

}
