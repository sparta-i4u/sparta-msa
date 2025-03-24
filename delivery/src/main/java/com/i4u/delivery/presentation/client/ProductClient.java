package com.i4u.delivery.presentation.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.i4u.delivery.presentation.dtos.response.OrderProductResponse;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

	@GetMapping("/api/v1/products/orders/{productId}/{productQuantity}")
	OrderProductResponse confirmProduct(@PathVariable UUID productId, @PathVariable Integer productQuantity);

}
