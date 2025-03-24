package com.i4u.hub.application.dtos.hub;

import com.i4u.hub.domain.model.Hub;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubListResDto {
    private List<HubDetailResDto> hubs;
    private PageInfo pageInfo;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageInfo {
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    public static HubListResDto from(List<Hub> hubs) {
        List<HubDetailResDto> hubDetails = hubs.stream().map(HubDetailResDto::from).collect(Collectors.toList());

        return HubListResDto.builder()
                .hubs(hubDetails)
                .build();
    }

    public static HubListResDto fromPage(Page<Hub> hubPage) {
        List<HubDetailResDto> hubDetails = hubPage.getContent().stream()
                .map(HubDetailResDto::from)
                .collect(Collectors.toList());

        PageInfo pageInfo = PageInfo.builder()
                .pageNumber(hubPage.getNumber())
                .pageSize(hubPage.getSize())
                .totalElements(hubPage.getTotalElements())
                .totalPages(hubPage.getTotalPages())
                .hasNext(hubPage.hasNext())
                .hasPrevious(hubPage.hasPrevious())
                .build();

        return HubListResDto.builder()
                .hubs(hubDetails)
                .pageInfo(pageInfo)
                .build();
    }
}
