package com.i4u.hub.application.dtos.hub;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateHubReqDto {
    private String hubName;
    private String address;
    private Double latitude;
    private Double longitude;
}
