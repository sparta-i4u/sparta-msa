package com.i4u.order.presentation.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/api/v1/products/orders/{productId}/{productQuantity}")
	OrderProductResponse confirmProduct(@PathVariable UUID productId, @PathVariable Integer productQuantity);

	@PatchMapping("/api/v1/products/orders/{beforeProductId}/{beforeProductQuantity}/{afterProductId}/{afterProductQuentity}")
	Map<String, Object> confirmProductUpdate(@PathVariable UUID beforeProductId, @PathVariable Integer beforeProductQuantity,
		@PathVariable UUID afterProductId, @PathVariable Integer afterProductQuantity);

	@PatchMapping("/api/v1/products/orders/canceled/{productId}/{productQuantity}")
	void updateProductState(@PathVariable UUID productId, @PathVariable Integer productQuantity);

}