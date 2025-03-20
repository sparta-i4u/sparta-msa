package com.i4u.hub.application.dtos.hub;

import com.i4u.hub.domain.model.Hub;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHubReqDto {
    private String hubName;     // 허브명 (센터명)
    private String address;     // 주소
    private Double latitude;    // 위도
    private Double longitude;   // 경도

    public Hub toEntity(UUID userId) {
        return Hub.builder()
                .hubName(hubName)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .managerId(userId)
                .build();
    }
}