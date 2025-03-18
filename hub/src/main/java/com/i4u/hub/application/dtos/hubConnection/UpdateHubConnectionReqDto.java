package com.i4u.hub.application.dtos.hubConnection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHubConnectionReqDto {
    private Integer hubToHubTime;
    private Integer distance;
}