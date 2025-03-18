package com.i4u.hub.application.dtos.hubConnection;

import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.model.HubConnection;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHubConnectionReqDto {
    private Hub departureHub;
    private Hub arrivalHub;
    private Integer hubToHubTime;
    private Integer distance;

    public HubConnection toEntity() {
        return HubConnection.builder()
                .departureHub(departureHub)
                .arrivalHub(arrivalHub)
                .hubToHubTime(hubToHubTime)
                .distance(distance)
                .build();
    }
}
