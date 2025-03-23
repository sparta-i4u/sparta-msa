package com.i4u.auth.application.dtos.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmUserResponse {

	private Boolean isDeleted;
	private UUID userId;
	private String userSlackId;
	private String userRole;
	private String email;

}

