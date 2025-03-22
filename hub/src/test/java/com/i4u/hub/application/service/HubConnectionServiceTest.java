package com.i4u.hub.application.service;

import com.i4u.hub.application.dtos.hubConnection.ShortestPathResDto;
import com.i4u.hub.infrastructure.config.HubConnectionDataInitializer;
import com.i4u.hub.infrastructure.config.HubDataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HubConnectionServiceTest {

    @Autowired
    HubConnectionService hubConnectionService;

    @Autowired
    private HubDataInitializer hubDataInitializer;

    @Autowired
    private HubConnectionDataInitializer hubConnectionDataInitializer;

    @BeforeEach
    void setUp() throws Exception {
        // 테스트 전 데이터 초기화
        hubDataInitializer.initHubData().run();
        hubConnectionDataInitializer.initHubConnectionData().run();
    }
    
    @Test
    void testFindShortestTimePath() {
        // 출발 허브와 도착 허브 이름을 사용하여 최소 이동 시간 경로 찾기
        ShortestPathResDto result = hubConnectionService.findShortestTimePath("서울특별시 센터", "대구광역시 센터");

        System.out.println("경로: ");
        result.getPath().forEach(hub -> System.out.println(hub.getHubName()));
        System.out.println("총 소요 시간: " + result.getTotalTime() + "분");
        
        assertNotNull(result);
        assertTrue(result.getTotalTime() > 0);
        assertFalse(result.getPath().isEmpty());
    }
}
