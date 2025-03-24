package com.i4u.product.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "HUB-SERVICE")
//hub로 요청을 쏴주는 친구 Client
public interface HubClient {

    //권한 검증 - 로그인한 사람 권한 검증
    // 받아주는 쪽 에서는 hubId가 있으면 UUID로 주고, 없으면 null을 반환할 예정
    @GetMapping("/api/v1/hubs/products/{userId}")
    public UUID getHubId(@PathVariable UUID userId);

    //상품을 생성할 때, request에 담긴 허브아이디가 진짜 해당 허브아이디인지 검증
    @GetMapping("/api/v1/hubs/products/{hubId}")
    public Boolean getHubIdByProduct(@PathVariable UUID hubId);
}
