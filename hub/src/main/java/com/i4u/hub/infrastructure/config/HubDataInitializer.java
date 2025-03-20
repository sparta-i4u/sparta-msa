package com.i4u.hub.infrastructure.config;

import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Order(1) // HubConnection 데이터가 저장되기 전에 실행되도록 순서 지정
public class HubDataInitializer {

    private final HubRepository hubRepository;

    @Bean
    public CommandLineRunner initHubData() {

        UUID hub1Uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID hub2Uuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID hub3Uuid = UUID.fromString("00000000-0000-0000-0000-000000000003");
        UUID hub4Uuid = UUID.fromString("00000000-0000-0000-0000-000000000004");
        UUID hub5Uuid = UUID.fromString("00000000-0000-0000-0000-000000000005");
        UUID hub6Uuid = UUID.fromString("00000000-0000-0000-0000-000000000006");
        UUID hub7Uuid = UUID.fromString("00000000-0000-0000-0000-000000000007");
        UUID hub8Uuid = UUID.fromString("00000000-0000-0000-0000-000000000008");
        UUID hub9Uuid = UUID.fromString("00000000-0000-0000-0000-000000000009");
        UUID hub10Uuid = UUID.fromString("00000000-0000-0000-0000-000000000010");
        UUID hub11Uuid = UUID.fromString("00000000-0000-0000-0000-000000000011");
        UUID hub12Uuid = UUID.fromString("00000000-0000-0000-0000-000000000012");
        UUID hub13Uuid = UUID.fromString("00000000-0000-0000-0000-000000000013");
        UUID hub14Uuid = UUID.fromString("00000000-0000-0000-0000-000000000014");
        UUID hub15Uuid = UUID.fromString("00000000-0000-0000-0000-000000000015");
        UUID hub16Uuid = UUID.fromString("00000000-0000-0000-0000-000000000016");
        UUID hub17Uuid = UUID.fromString("00000000-0000-0000-0000-000000000017");

        List<Hub> hublist = Arrays.asList(
                Hub.builder().hubName("서울특별시 센터").address("서울특별시 송파구 송파대로 55")
                        .latitude(37.4742027808565).longitude(127.123621185562).managerId(hub1Uuid).build(),
                Hub.builder().hubName("경기 북부 센터").address("경기도 고양시 덕양구 권율대로 570")
                        .latitude(37.6403771056018).longitude(126.87379545786).managerId(hub2Uuid).build(),
                Hub.builder().hubName("경기 남부 센터").address("경기도 이천시 덕평로 257-21")
                        .latitude(37.1896213142136).longitude(127.375050006958).managerId(hub3Uuid).build(),
                Hub.builder().hubName("부산광역시 센터").address("부산 동구 중앙대로 206")
                        .latitude(35.117605126596).longitude(129.045060216345).managerId(hub4Uuid).build(),
                Hub.builder().hubName("대구광역시 센터").address("대구 북구 태평로 161")
                        .latitude(35.8758849492106).longitude(128.596129208483).managerId(hub5Uuid).build(),
                Hub.builder().hubName("인천광역시 센터").address("인천 남동구 정각로 29")
                        .latitude(37.4560499608337).longitude(126.705255744089).managerId(hub6Uuid).build(),
                Hub.builder().hubName("광주광역시 센터").address("광주 서구 내방로 111")
                        .latitude(35.1600994105234).longitude(126.851461925213).managerId(hub7Uuid).build(),
                Hub.builder().hubName("대전광역시 센터").address("대전 서구 둔산로 100")
                        .latitude(36.3503849976553).longitude(127.384633005948).managerId(hub8Uuid).build(),
                Hub.builder().hubName("울산광역시 센터").address("울산 남구 중앙로 201")
                        .latitude(35.5390270962011).longitude(129.311356392207).managerId(hub9Uuid).build(),
                Hub.builder().hubName("세종특별자치시 센터").address("세종특별자치시 한누리대로 2130")
                        .latitude(36.4800579897497).longitude(127.289039408864).managerId(hub10Uuid).build(),
                Hub.builder().hubName("강원특별자치도 센터").address("강원특별자치도 춘천시 중앙로 1")
                        .latitude(37.8800729197963).longitude(127.727907820318).managerId(hub11Uuid).build(),
                Hub.builder().hubName("충청북도 센터").address("충북 청주시 상당구 상당로 82")
                        .latitude(36.6353867908159).longitude(127.491428436987).managerId(hub12Uuid).build(),
                Hub.builder().hubName("충청남도 센터").address("충남 홍성군 홍북읍 충남대로 21")
                        .latitude(36.6590416999343).longitude(126.673057036952).managerId(hub13Uuid).build(),
                Hub.builder().hubName("전북특별자치도 센터").address("전북특별자치도 전주시 완산구 효자로 225")
                        .latitude(35.8194621650578).longitude(127.106396942356).managerId(hub14Uuid).build(),
                Hub.builder().hubName("전라남도 센터").address("전남 무안군 삼향읍 오룡길 1")
                        .latitude(34.8174727676363).longitude(126.465415935304).managerId(hub15Uuid).build(),
                Hub.builder().hubName("경상북도 센터").address("경북 안동시 풍천면 도청대로 455")
                        .latitude(36.5761205474728).longitude(128.505722686385).managerId(hub16Uuid).build(),
                Hub.builder().hubName("경상남도 센터").address("경남 창원시 의창구 중앙대로 300")
                        .latitude(35.2378032514675).longitude(128.691940442146).managerId(hub17Uuid).build()
        );

        return args -> hubRepository.saveAll(hublist);
    }
}