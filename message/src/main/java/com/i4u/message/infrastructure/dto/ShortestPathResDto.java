package com.i4u.message.infrastructure.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortestPathResDto {
    private int totalTime;
    private List<PathHubDto> path;
    private String departureHubName;
    private String arrivalHubName;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PathHubDto {
        private UUID hubId;
        private String hubName;

    }

}

