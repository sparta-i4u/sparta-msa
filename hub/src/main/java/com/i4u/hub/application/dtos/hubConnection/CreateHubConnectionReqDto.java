package com.i4u.hub.application.dtos.hubConnection;

import com.i4u.hub.domain.model.HubConnection;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHubConnectionReqDto {
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Integer hubToHubTime;
    private Integer distance;

    public HubConnection toEntity() {
        return HubConnection.builder()
                .departure_hub_id(departureHubId)
                .arrival_hub_id(arrivalHubId)
                .hub_to_hub_time(hubToHubTime)
                .distance(distance)
                .build();
    }
}
