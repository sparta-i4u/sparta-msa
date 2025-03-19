package com.i4u.gateway.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/gateway")
public class ApiGatewayController {

	private final WebClient.Builder webClientBuilder;

	public ApiGatewayController(WebClient.Builder webClientBuilder) {
		this.webClientBuilder = webClientBuilder;
	}

	@GetMapping("/shipper-info/{userId}/{hubId}")
	public Mono<Map<String, Object>> getShipperInfo(@PathVariable UUID userId, @PathVariable UUID hubId) {
		WebClient userClient = webClientBuilder.baseUrl("http://user-service:19092").build();
		WebClient hubClient = webClientBuilder.baseUrl("http://hub-service:19060").build();

		Mono<Map<String, Object>> userResponse = userClient.get()
			.uri("/api/v1/users/shippers/" + userId)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<>() {
			});

		Mono<Map<String, Object>> hubResponse = hubClient.get()
			.uri("/api/v1/hubs/shippers/" + hubId)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<>() {
			});

		return Mono.zip(userResponse, hubResponse)
			.map(tuple -> {
				Map<String, Object> response = new HashMap<>();
				response.put("userId", tuple.getT1().get("userId"));
				response.put("userSlackId", tuple.getT1().get("userSlackId"));
				response.put("userIsDeleted", tuple.getT1().get("isDeleted"));
				response.put("hubId", tuple.getT2().get("hubId"));
				response.put("hubManagerId", tuple.getT2().get("hubManagerId"));
				response.put("hubIsDeleted", tuple.getT2().get("isDeleted"));
				return response;
			});

	}

	@GetMapping("/shipper-info/{hubId}")
	public Mono<Map<String, Object>> getShipperInfo(@PathVariable UUID hubId) {
		WebClient hubClient = webClientBuilder.baseUrl("http://hub-service:19060").build();

		Mono<Map<String, Object>> hubResponse = hubClient.get()
			.uri("/api/v1/hubs/shippers/" + hubId)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<>() {
			});

		return hubResponse;
	}

}
