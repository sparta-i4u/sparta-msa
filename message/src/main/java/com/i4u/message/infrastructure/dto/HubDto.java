package com.i4u.message.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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
