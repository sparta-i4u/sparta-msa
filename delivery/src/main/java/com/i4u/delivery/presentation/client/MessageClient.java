package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.i4u.shipper.presentation.dtos.request.MessageRequest;

@FeignClient(name = "MESSAGE-SERVICE")
public interface MessageClient {

	@PostMapping("/api/v1/messges/deliveries")
	void sendInfoToMessage(@RequestBody MessageRequest request);

}
