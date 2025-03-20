package com.i4u.company.presentation.client;

import com.i4u.company.presentation.dtos.response.ConfirmUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/v1/auth/{userId}")
    ConfirmUserResponse confirmUser(@PathVariable UUID userId);

}