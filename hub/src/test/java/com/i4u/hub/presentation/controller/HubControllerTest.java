package com.i4u.hub.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4u.hub.application.dtos.hub.CreateHubReqDto;
import com.i4u.hub.application.dtos.hub.UpdateHubReqDto;
import com.i4u.hub.application.service.HubService;
import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.repository.HubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HubRepository hubRepository;

    @Autowired
    private HubService hubService;

    private UUID testUserId;
    private UUID testHubId;
    private Hub testHub;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        // 테스트용 허브 생성
        testHub = Hub.builder()
                .hubName("테스트 허브")
                .address("서울시 테스트구 테스트동 123")
                .latitude(37.5665)
                .longitude(126.9780)
                .managerId(testUserId)
                .build();

        hubRepository.save(testHub);
        testHubId = testHub.getHubId();
    }

    @Test
    @DisplayName("허브 생성 API 테스트")
    void createHubTest() throws Exception {
        // given
        CreateHubReqDto createHubReqDto = CreateHubReqDto.builder()
                .hubName("새로운 허브")
                .address("서울시 강남구 역삼동 123")
                .latitude(37.5012)
                .longitude(127.0396)
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/hubs")
                .header("X-User-Id", testUserId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createHubReqDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("C000"))
                .andExpect(jsonPath("$.message").value("허브가 등록되었습니다."))
                .andExpect(jsonPath("$.data.hubName").value("새로운 허브"))
                .andDo(print());
    }

    @Test
    @DisplayName("허브 조회 API 테스트")
    void getHubTest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/hubs/{hubId}", testHubId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.data.hubId").value(testHubId.toString()))
                .andExpect(jsonPath("$.data.hubName").value("테스트 허브"))
                .andDo(print());
    }

    @Test
    @DisplayName("허브 목록 전체 조회 API 테스트")
    void getAllHubsTest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/hubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.data.hubs").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("허브 목록 페이지네이션 조회 API 테스트")
    void getHubsWithPaginationTest() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/hubs")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.data.hubs").isArray())
                .andExpect(jsonPath("$.data.pageInfo").exists())
                .andExpect(jsonPath("$.data.pageInfo.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageInfo.pageSize").value(10))
                .andDo(print());
    }

    @Test
    @DisplayName("허브 수정 API 테스트")
    void updateHubTest() throws Exception {
        // given
        UpdateHubReqDto updateHubReqDto = UpdateHubReqDto.builder()
                .hubName("수정된 허브명")
                .address("서울시 수정구 수정동 456")
                .latitude(37.6012)
                .longitude(127.1396)
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/hubs/{hubId}", testHubId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateHubReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.data.hubName").value("수정된 허브명"))
                .andExpect(jsonPath("$.data.address").value("서울시 수정구 수정동 456"))
                .andDo(print());
    }

    @Test
    @DisplayName("허브 삭제 API 테스트")
    void deleteHubTest() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/hubs/{hubId}", testHubId)
                .header("X-User-Id", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("S000"))
                .andExpect(jsonPath("$.message").value("허브 삭제가 완료되었습니다."))
                .andDo(print());
    }
}