package com.i4u.hub.application.dtos;

import com.i4u.hub.domain.model.Hub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubDetailResDto {
    private UUID hubId;              // 생성된 허브 ID (UUID)
    private String hubName;          // 요청에서 받은 허브명
    private String address;          // 요청에서 받은 주소
    private Double latitude;         // 요청에서 받은 위도
    private Double longitude;        // 요청에서 받은 경도
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간

    public static HubDetailResDto from (Hub hub) {
        return HubDetailResDto.builder()
                .hubId(hub.getHubId())
                .hubName(hub.getHubName())
                .address(hub.getAddress())
                .latitude(hub.getLatitude())
                .longitude(hub.getLongitude())
                .createdAt(hub.getCreatedAt())
                .updatedAt(hub.getUpdatedAt())
                .build();
    }

}
