package com.i4u.order.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.i4u.order.presentation.dtos.request.OrderProductRequest;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;

@FeignClient(name = "product")
public interface ProductClient {

	@GetMapping("/products/{productId")
	OrderProductResponse confirmProduct(@ModelAttribute OrderProductRequest request /*userId, userRole or JWT 필요*/);

}