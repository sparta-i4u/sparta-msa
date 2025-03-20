package com.i4u.order.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.i4u.order.presentation.dtos.request.OrderProductRequest;
import com.i4u.order.presentation.dtos.request.OrderProductStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderProductUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;
import com.i4u.order.presentation.dtos.response.OrderProductUpdateResponse;

@FeignClient(name = "product")
public interface ProductClient {

	@GetMapping("/products/{productId}")
	OrderProductResponse confirmProduct(@ModelAttribute OrderProductRequest request /*userId, userRole or JWT 필요*/);

	@GetMapping("/products/{productId}/new-info")
	OrderProductUpdateResponse confirmProductUpdate(OrderProductUpdateRequest build);

	@GetMapping("/products/{productId}/update-product")
	void updateProductState(OrderProductStateUpdateRequest build);
}