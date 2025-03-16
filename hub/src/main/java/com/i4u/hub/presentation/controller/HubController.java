package com.i4u.hub.presentation.controller;

import com.i4u.hub.application.dtos.ApiResponse;
import com.i4u.hub.application.dtos.CreateHubReqDto;
import com.i4u.hub.application.dtos.HubDetailResDto;
import com.i4u.hub.application.dtos.HubListResDto;
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
        // Hub 생성 로직
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

        // 허브 목록 조회 로직
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
    @PutMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubDetailResDto>> updateHub(@PathVariable String hubId, @RequestBody CreateHubReqDto hubReqDto) {
        // 구현해야 할 서비스 로직
        // HubDetailResDto responseDto = hubService.updateHub(hubId, hubReqDto);
        return ResponseEntity.ok(ApiResponse.success(null, "허브 수정이 완료되었습니다."));
    }

    /**
     * 허브 삭제 API
     *
     * @param hubId 허브 ID
     * @return 허브 삭제 응답 DTO
     */
    @DeleteMapping("/{hubId}")
    public ResponseEntity<ApiResponse<HubDetailResDto>> deleteHub(@PathVariable String hubId) {
        // 구현해야 할 서비스 로직
        // hubService.deleteHub(hubId);
        return ResponseEntity.ok(ApiResponse.success(null, "허브 삭제가 완료되었습니다."));
    }

}