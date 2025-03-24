package com.i4u.message.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

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