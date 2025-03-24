package com.i4u.hub.presentation.controller;

import com.i4u.common.utils.CommonResponse;
import com.i4u.hub.application.dtos.hub.CreateHubReqDto;
import com.i4u.hub.application.dtos.hub.HubDetailResDto;
import com.i4u.hub.application.dtos.hub.HubListResDto;
import com.i4u.hub.application.dtos.hub.HubPageReqDto;
import com.i4u.hub.application.dtos.hub.UpdateHubReqDto;
import com.i4u.hub.application.service.HubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;

    /**
     * 허브 생성 API
     *
     * @param createHubReqDto 허브 생성 요청 DTO
     * @return 허브 생성 응답 DTO
     */
    @PostMapping
    public ResponseEntity<CommonResponse<HubDetailResDto>> createHub(@RequestBody CreateHubReqDto createHubReqDto,
                                                                      @RequestHeader(value = "X-User-Id") UUID userId) {
        HubDetailResDto responseDto = hubService.createHub(createHubReqDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.created(responseDto, "허브가 등록되었습니다."));
    }

    /**
     * 허브 조회 API
     *
     * @return 허브 조회 응답 DTO
     */
    @GetMapping("/{hubId}")
    public ResponseEntity<CommonResponse<HubDetailResDto>> getHub(@PathVariable UUID hubId) {
        HubDetailResDto responseDto = hubService.getHub(hubId);

        return ResponseEntity.ok(CommonResponse.success(responseDto, "허브 조회가 완료되었습니다."));
    }

    /**
     * 허브 목록 조회 API
     *
     * @return 허브 목록 조회 응답 DTO
     */
    @GetMapping
    public ResponseEntity<CommonResponse<HubListResDto>> getHubs(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        
        // 페이지네이션 파라미터가 있으면 페이지네이션 적용
        if (page != null || size != null) {
            HubPageReqDto pageReqDto = HubPageReqDto.of(page, size);
            HubListResDto responseDto = hubService.getHubsWithPagination(pageReqDto);
            return ResponseEntity.ok(CommonResponse.success(responseDto, "허브 페이지 조회가 완료되었습니다."));
        }
        
        // 페이지네이션 파라미터가 없으면 전체 조회
        HubListResDto responseDto = hubService.getHubs();
        return ResponseEntity.ok(CommonResponse.success(responseDto, "허브 전체 조회가 완료되었습니다."));
    }

    /**
     * 허브 수정 API
     *
     * @param hubId    허브 ID
     * @param hubReqDto 허브 수정 요청 DTO
     * @return 허브 수정 응답 DTO
     */
    @PatchMapping("/{hubId}")
    public ResponseEntity<CommonResponse<HubDetailResDto>> updateHub(@PathVariable UUID hubId, @RequestBody UpdateHubReqDto hubReqDto) {
        HubDetailResDto updatedHub = hubService.updateHub(hubId, hubReqDto);

        return ResponseEntity.ok(CommonResponse.success(updatedHub, "허브 수정이 완료되었습니다."));
    }

    /**
     * 허브 삭제 API
     *
     * @param hubId 허브 ID
     * @return 허브 삭제 응답 DTO
     */
    @DeleteMapping("/{hubId}")
    public ResponseEntity<CommonResponse<HubDetailResDto>> deleteHub(@PathVariable UUID hubId,
                                                                     @RequestHeader(value = "X-User-Id") UUID userId) {
        hubService.deleteHub(hubId, userId);

        return ResponseEntity.ok(CommonResponse.success(null, "허브 삭제가 완료되었습니다."));
    }
}
