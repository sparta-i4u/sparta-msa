package com.i4u.hub.application.dtos;

import com.i4u.hub.domain.model.Hub;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubListResDto {
    List<HubDetailResDto> hubs;

    public static HubListResDto from(List<Hub> hubs) {
        List<HubDetailResDto> hubDetails = hubs.stream().map(HubDetailResDto::from).collect(Collectors.toList());

        return HubListResDto.builder()
                .hubs(hubDetails)
                .build();
    }
}
