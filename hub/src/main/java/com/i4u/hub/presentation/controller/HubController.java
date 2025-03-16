package com.i4u.hub.presentation.controller;

import com.i4u.hub.application.dtos.*;
import com.i4u.hub.application.service.HubService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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
    public ResponseEntity<ApiResponse<HubDetailResDto>> createHub(@RequestBody CreateHubReqDto createHubReqDto) {
        HubDetailResDto responseDto = hubService.createHub(createHubReqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(responseDto, "허브가 등록되었습니다."));

    }
    /**
     * 허브 조회 API
     *
     * @return 허브 조회 응답 DTO
     */
    @GetMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubDetailResDto>> getHub(@PathVariable UUID hubId) {
        HubDetailResDto responseDto = hubService.getHub(hubId);

        return ResponseEntity.ok(ApiResponse.success(responseDto, "허브 조회가 완료되었습니다."));
    }

    /**
     * 허브 목록 조회 API
     *
     * @return 허브 목록 조회 응답 DTO
     */
    @GetMapping
    public ResponseEntity<ApiResponse<HubListResDto>> getHubs() {
         HubListResDto responseDto = hubService.getHubs();

        return ResponseEntity.ok(ApiResponse.success(responseDto, "허브 전체 조회가 완료되었습니다."));
    }

    /**
     * 허브 수정 API
     *
     * @param hubId    허브 ID
     * @param hubReqDto 허브 수정 요청 DTO
     * @return 허브 수정 응답 DTO
     */
    @PatchMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubDetailResDto>> updateHub(@PathVariable UUID hubId, @RequestBody UpdateHubReqDto hubReqDto) {
        HubDetailResDto updatedHub = hubService.updateHub(hubId, hubReqDto);

        return ResponseEntity.ok(ApiResponse.success(updatedHub, "허브 수정이 완료되었습니다."));
    }

    /**
     * 허브 삭제 API
     *
     * @param hubId 허브 ID
     * @return 허브 삭제 응답 DTO
     */
    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubDetailResDto>> deleteHub(@PathVariable UUID hubId) {
        hubService.deleteHub(hubId);

        return ResponseEntity.ok(ApiResponse.success(null, "허브 삭제가 완료되었습니다."));
    }

}