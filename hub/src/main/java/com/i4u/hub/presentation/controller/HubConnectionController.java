package com.i4u.hub.presentation.controller;

import com.i4u.common.utils.CommonResponse;
import com.i4u.hub.application.dtos.hubConnection.CreateHubConnectionReqDto;
import com.i4u.hub.application.dtos.hubConnection.HubConnectionListResDto;
import com.i4u.hub.application.dtos.hubConnection.HubConnectionResDto;
import com.i4u.hub.application.dtos.hubConnection.UpdateHubConnectionReqDto;
import com.i4u.hub.application.service.HubConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hub-connections")
@RequiredArgsConstructor
public class HubConnectionController {

    private final HubConnectionService hubConnectionService;

    /**
     * 허브간 이동정보 생성 API
     *
     * @param createHubConnectionReqDto 허브간 이동정보 생성 요청 DTO
     * @return 허브간 이동정보 생성 응답 DTO
     */
    @PostMapping
    public ResponseEntity<CommonResponse<HubConnectionResDto>> createHubConnection(@RequestBody CreateHubConnectionReqDto createHubConnectionReqDto) {
        HubConnectionResDto responseDto = hubConnectionService.createHubConnection(createHubConnectionReqDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.created(responseDto, "허브간 이동정보가 등록되었습니다."));
    }

    /**
     * 허브간 이동정보 조회 API
     *
     * @param hubConnectionId 허브 이동정보 ID
     * @return 허브간 이동정보 조회 응답 DTO
     */
    @GetMapping("/{hubConnectionId}")
    public ResponseEntity<CommonResponse<HubConnectionResDto>> getHubConnection(@PathVariable UUID hubConnectionId) {
        HubConnectionResDto responseDto = hubConnectionService.getHubConnection(hubConnectionId);

        return ResponseEntity.ok(CommonResponse.success(responseDto, "허브간 이동정보 조회가 완료되었습니다."));
    }

    /**
     * 허브간 이동정보 전체 조회 API
     *
     * @return 허브간 이동정보 목록 조회 응답 DTO
     */
    @GetMapping
    public ResponseEntity<CommonResponse<HubConnectionListResDto>> getHubConnections() {
        HubConnectionListResDto responseDto = hubConnectionService.getHubConnections();

        return ResponseEntity.ok(CommonResponse.success(responseDto, "허브간 이동정보 전체 조회가 완료되었습니다."));
    }

    /**
     * 허브간 이동정보 수정 API
     *
     * @param hubConnectionId 허브 이동정보 ID
     * @param updateHubConnectionReqDto 허브간 이동정보 수정 요청 DTO
     * @return 허브간 이동정보 수정 응답 DTO
     */
    @PatchMapping("/{hubConnectionId}")
    public ResponseEntity<CommonResponse<HubConnectionResDto>> updateHubConnection(
            @PathVariable UUID hubConnectionId,
            @RequestBody UpdateHubConnectionReqDto updateHubConnectionReqDto) {
        HubConnectionResDto updatedConnection = hubConnectionService.updateHubConnection(hubConnectionId, updateHubConnectionReqDto);

        return ResponseEntity.ok(CommonResponse.success(updatedConnection, "허브간 이동정보 수정이 완료되었습니다."));
    }

    /**
     * 허브간 이동정보 삭제 API
     *
     * @param hubConnectionId 허브 이동정보 ID
     * @return 허브간 이동정보 삭제 응답 DTO
     */
    @DeleteMapping("/{hubConnectionId}")
    public ResponseEntity<CommonResponse<Void>> deleteHubConnection(@PathVariable UUID hubConnectionId) {
        hubConnectionService.deleteHubConnection(hubConnectionId);

        return ResponseEntity.ok(CommonResponse.success(null, "허브간 이동정보 삭제가 완료되었습니다."));
    }
}