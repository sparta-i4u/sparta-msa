package com.i4u.hub.infrastructure.config;

import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.model.HubConnection;
import com.i4u.hub.domain.repository.HubConnectionRepository;
import com.i4u.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Order(2) // Hub 데이터가 먼저 저장된 후 실행되도록 순서 지정
public class HubConnectionDataInitializer {

    private final HubRepository hubRepository;
    private final HubConnectionRepository hubConnectionRepository;

    @Bean
    public CommandLineRunner initHubConnectionData() {
        return args -> {
            Map<String, Hub> hubMap = hubRepository.findAll().stream()
                    .collect(Collectors.toMap(Hub::getHubName, hub -> hub));

            List<HubConnection> connections = new ArrayList<>();

            // 경기 남부 센터 연결
            addConnection(connections, hubMap, "경기 남부 센터", "경기 북부 센터", 90, 69);
            addConnection(connections, hubMap, "경기 남부 센터", "서울특별시 센터", 52, 52);
            addConnection(connections, hubMap, "경기 남부 센터", "인천광역시 센터", 80, 66);
            addConnection(connections, hubMap, "경기 남부 센터", "강원특별자치도 센터", 139, 119);
            addConnection(connections, hubMap, "경기 남부 센터", "경상북도 센터", 186, 155);
            addConnection(connections, hubMap, "경기 남부 센터", "대전광역시 센터", 112, 96);
            addConnection(connections, hubMap, "경기 남부 센터", "대구광역시 센터", 240, 187);

            // 대전광역시 센터 연결
            addConnection(connections, hubMap, "대전광역시 센터", "충청남도 센터", 87, 63);
            addConnection(connections, hubMap, "대전광역시 센터", "충청북도 센터", 39, 46);
            addConnection(connections, hubMap, "대전광역시 센터", "세종특별자치시 센터", 22, 25);
            addConnection(connections, hubMap, "대전광역시 센터", "전북특별자치도 센터", 85, 74);
            addConnection(connections, hubMap, "대전광역시 센터", "광주광역시 센터", 168, 113);
            addConnection(connections, hubMap, "대전광역시 센터", "전라남도 센터", 229, 152);
            addConnection(connections, hubMap, "대전광역시 센터", "경기 남부 센터", 110, 86);
            addConnection(connections, hubMap, "대전광역시 센터", "대구광역시 센터", 151, 106);

            // 대구광역시 센터 연결
            addConnection(connections, hubMap, "대구광역시 센터", "경상북도 센터", 112, 74);
            addConnection(connections, hubMap, "대구광역시 센터", "경상남도 센터", 94, 75);
            addConnection(connections, hubMap, "대구광역시 센터", "부산광역시 센터", 112, 81);
            addConnection(connections, hubMap, "대구광역시 센터", "울산광역시 센터", 109, 87);
            addConnection(connections, hubMap, "대구광역시 센터", "경기 남부 센터", 235, 155);
            addConnection(connections, hubMap, "대구광역시 센터", "대전광역시 센터", 152, 105);

            // 경상북도 센터 연결
            addConnection(connections, hubMap, "경상북도 센터", "경기 남부 센터", 200, 132);
            addConnection(connections, hubMap, "경상북도 센터", "대구광역시 센터", 104, 75);

            hubConnectionRepository.saveAll(connections);
        };
    }

    private void addConnection(List<HubConnection> connections, Map<String, Hub> hubMap,
                              String departureName, String arrivalName, Integer distance, Integer timeMinutes) {
        Hub departure = hubMap.get(departureName);
        Hub arrival = hubMap.get(arrivalName);

        if (departure != null && arrival != null) {
            HubConnection connection = HubConnection.builder()
                    .departure_hub_id(departure.getHubId())
                    .arrival_hub_id(arrival.getHubId())
                    .distance(distance)
                    .hub_to_hub_time(timeMinutes)
                    .build();

            connections.add(connection);
        }
    }
}