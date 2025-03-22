package com.i4u.hub.application.dtos.hubConnection;

import com.i4u.hub.domain.model.Hub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        
        public static PathHubDto from(Hub hub) {
            return PathHubDto.builder()
                    .hubId(hub.getHubId())
                    .hubName(hub.getHubName())
                    .build();
        }
    }
    
    public static ShortestPathResDto from(
            int totalTime, 
            List<Hub> hubPath,
            String departureHubName, 
            String arrivalHubName) {
        
        List<PathHubDto> pathDtos = hubPath.stream()
                .map(PathHubDto::from)
                .collect(Collectors.toList());
        
        return ShortestPathResDto.builder()
                .totalTime(totalTime)
                .path(pathDtos)
                .departureHubName(departureHubName)
                .arrivalHubName(arrivalHubName)
                .build();
    }
}
