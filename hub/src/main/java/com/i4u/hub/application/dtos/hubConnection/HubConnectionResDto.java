package com.i4u.hub.application.dtos.hubConnection;

import com.i4u.hub.domain.model.HubConnection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubConnectionResDto {
    private UUID hubConnectionId;
    private UUID departureHubId;
    private UUID arrivalHubId;
    private Integer hubToHubTime;
    private Integer distance;

    public static HubConnectionResDto from(HubConnection hubConnection) {
        return HubConnectionResDto.builder()
                .hubConnectionId(hubConnection.getHubConnectionId())
                .departureHubId(hubConnection.getDepartureHub().getHubId())
                .arrivalHubId(hubConnection.getArrivalHub().getHubId())
                .hubToHubTime(hubConnection.getHubToHubTime())
                .distance(hubConnection.getDistance())
                .build();
    }
}