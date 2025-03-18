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
                .hubConnectionId(hubConnection.getHub_connection_id())
                .departureHubId(hubConnection.getDeparture_hub_id())
                .arrivalHubId(hubConnection.getArrival_hub_id())
                .hubToHubTime(hubConnection.getHub_to_hub_time())
                .distance(hubConnection.getDistance())
                .build();
    }
}