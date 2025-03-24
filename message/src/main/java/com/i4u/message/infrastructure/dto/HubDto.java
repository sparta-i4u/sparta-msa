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
    private UUID hubId;
    private String hubName;
    private String address;
    private Double latitude;
    private Double longitude;
    private UUID managerId;
}
