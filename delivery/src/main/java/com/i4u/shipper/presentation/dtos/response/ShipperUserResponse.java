package com.i4u.shipper.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperUserResponse {

	private Boolean isDeleted;
	private UUID userId;
	private String userSlackId;

}
