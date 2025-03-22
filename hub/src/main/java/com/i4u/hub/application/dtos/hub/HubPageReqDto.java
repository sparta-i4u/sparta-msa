package com.i4u.hub.application.dtos.hub;

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
public class HubPageReqDto {
    private int page; // 페이지 번호 (0부터 시작)
    private int size; // 페이지 크기 (10, 30, 50)
    
    public static HubPageReqDto of(Integer page, Integer size) {
        int pageNum = (page == null || page < 0) ? 0 : page;
        int pageSize = validatePageSize(size);
        
        return HubPageReqDto.builder()
                .page(pageNum)
                .size(pageSize)
                .build();
    }
    
    // 페이지 크기는 10, 30, 50 중 하나여야 함
    private static int validatePageSize(Integer size) {
        if (size == null) {
            return 10; // 기본값
        }
        
        return switch (size) {
            case 10, 30, 50 -> size;
            default -> 10; // 유효하지 않은 값은 기본값으로
        };
    }
}
