package com.i4u.order.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.presentation.dtos.request.OrderProductRequest;
import com.i4u.order.presentation.dtos.request.OrderProductStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderProductUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;
import com.i4u.order.presentation.dtos.response.OrderProductUpdateResponse;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

	@GetMapping("/api/v1/products/order")
	ResponseEntity<CommonResponse<OrderProductResponse>> confirmProduct(@ModelAttribute OrderProductRequest request /*userId, userRole or JWT 필요*/);

	@PatchMapping("/api/v1/products/{productId}/update-product")
	ResponseEntity<CommonResponse<OrderProductUpdateResponse>> confirmProductUpdate(@RequestBody OrderProductUpdateRequest request);

	@PatchMapping("/api/v1/products/{productId}/update-product")
	ResponseEntity<CommonResponse> updateProductState(@RequestBody OrderProductStateUpdateRequest request);

}