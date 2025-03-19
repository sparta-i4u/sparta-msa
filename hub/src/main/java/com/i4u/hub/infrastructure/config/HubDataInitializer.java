package com.i4u.hub.infrastructure.config;

import com.i4u.hub.domain.model.Hub;
import com.i4u.hub.domain.repository.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class HubDataInitializer {

    private final HubRepository hubRepository;

    @Bean
    public CommandLineRunner initHubData() {
        List<Hub> hublist = Arrays.asList(
                Hub.builder().hubName("서울특별시 센터").address("서울특별시 송파구 송파대로 55")
                        .latitude(37.4742027808565).longitude(127.123621185562).build(),
                Hub.builder().hubName("경기 북부 센터").address("경기도 고양시 덕양구 권율대로 570")
                        .latitude(37.6403771056018).longitude(126.87379545786).build(),
                Hub.builder().hubName("경기 남부 센터").address("경기도 이천시 덕평로 257-21")
                        .latitude(37.1896213142136).longitude(127.375050006958).build(),
                Hub.builder().hubName("부산광역시 센터").address("부산 동구 중앙대로 206")
                        .latitude(35.117605126596).longitude(129.045060216345).build(),
                Hub.builder().hubName("대구광역시 센터").address("대구 북구 태평로 161")
                        .latitude(35.8758849492106).longitude(128.596129208483).build(),
                Hub.builder().hubName("인천광역시 센터").address("인천 남동구 정각로 29")
                        .latitude(37.4560499608337).longitude(126.705255744089).build(),
                Hub.builder().hubName("광주광역시 센터").address("광주 서구 내방로 111")
                        .latitude(35.1600994105234).longitude(126.851461925213).build(),
                Hub.builder().hubName("대전광역시 센터").address("대전 서구 둔산로 100")
                        .latitude(36.3503849976553).longitude(127.384633005948).build(),
                Hub.builder().hubName("울산광역시 센터").address("울산 남구 중앙로 201")
                        .latitude(35.5390270962011).longitude(129.311356392207).build(),
                Hub.builder().hubName("세종특별자치시 센터").address("세종특별자치시 한누리대로 2130")
                        .latitude(36.4800579897497).longitude(127.289039408864).build(),
                Hub.builder().hubName("강원특별자치도 센터").address("강원특별자치도 춘천시 중앙로 1")
                        .latitude(37.8800729197963).longitude(127.727907820318).build(),
                Hub.builder().hubName("충청북도 센터").address("충북 청주시 상당구 상당로 82")
                        .latitude(36.6353867908159).longitude(127.491428436987).build(),
                Hub.builder().hubName("충청남도 센터").address("충남 홍성군 홍북읍 충남대로 21")
                        .latitude(36.6590416999343).longitude(126.673057036952).build(),
                Hub.builder().hubName("전북특별자치도 센터").address("전북특별자치도 전주시 완산구 효자로 225")
                        .latitude(35.8194621650578).longitude(127.106396942356).build(),
                Hub.builder().hubName("전라남도 센터").address("전남 무안군 삼향읍 오룡길 1")
                        .latitude(34.8174727676363).longitude(126.465415935304).build(),
                Hub.builder().hubName("경상북도 센터").address("경북 안동시 풍천면 도청대로 455")
                        .latitude(36.5761205474728).longitude(128.505722686385).build(),
                Hub.builder().hubName("경상남도 센터").address("경남 창원시 의창구 중앙대로 300")
                        .latitude(35.2378032514675).longitude(128.691940442146).build()
        );

        return args -> hubRepository.saveAll(hublist);
    }
}