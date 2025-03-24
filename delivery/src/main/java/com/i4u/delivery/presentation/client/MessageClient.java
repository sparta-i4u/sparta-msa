package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.presentation.dtos.response.MessageResDto;
import com.i4u.shipper.presentation.dtos.request.MessageRequest;

@FeignClient(name = "MESSAGE-SERVICE")
public interface MessageClient {

	@PostMapping("/api/v1/messages/AI-slack")
	ResponseEntity<CommonResponse<MessageResDto>> sendInfoToMessage(@RequestBody MessageRequest request);

}
