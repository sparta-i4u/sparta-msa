package com.i4u.company.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/api/v1/hubs/companies/{hubId}")
    UUID getHubInfo(@PathVariable("hubId") UUID hubId);

}
