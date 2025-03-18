package com.i4u.hub.application.dtos.hubConnection;

import com.i4u.hub.domain.model.HubConnection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubConnectionListResDto {
    private List<HubConnection> hubConnections;

    public static HubConnectionListResDto from(List<HubConnection> hubConnections) {
        return HubConnectionListResDto.builder()
                .hubConnections(hubConnections)
                .build();
    }
}