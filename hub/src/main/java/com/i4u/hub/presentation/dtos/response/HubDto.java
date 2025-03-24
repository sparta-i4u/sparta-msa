package com.i4u.hub.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubDto {
	private String supplierHubName;
	private String supplierHubAddress;
	private Double supplierHubLatitude;
	private Double supplierHubLongitude;
	private UUID supplierHubManagerId;

	private String recipientHubName;
	private String recipientHubAddress;
	private Double recipientHubLatitude;
	private Double recipientHubLongitude;
}

