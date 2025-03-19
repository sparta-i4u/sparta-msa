package com.i4u.hub.presentation.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipperHubResponse {

	private Boolean isDeleted;
	private UUID hubId;
	private UUID hubManagerId;

}
