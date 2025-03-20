package com.i4u.product.presentation.endpoint;

import com.i4u.common.utils.CommonResponse;
import com.i4u.product.application.service.ProductClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductClientService productClientService;

    @GetMapping("/api/v1/products/orders/{productId}/{productQuantity}")
    public Mono<Map<String, Object>> confirmProduct(@PathVariable UUID productId, @PathVariable Integer productQuantity) {
        Map<String, Object> response = productClientService.confirmProduct(productId, productQuantity);
        return Mono.just(response);
    }

    @PatchMapping("/api/v1/products/orders/{beforeProductId}/{beforeProductQuantity}/{afterProductId}/{afterProductQuentity}")
    public Mono<Map<String, Object>> confirmProductUpdate(@PathVariable UUID beforeProductId, @PathVariable Integer beforeProductQuantity,
                                                                                    @PathVariable UUID afterProductId, @PathVariable Integer afterProductQuantity) {
        Map<String, Object> response = productClientService.confirmProductUpdate(beforeProductId, beforeProductQuantity, afterProductId, afterProductQuantity);
        return Mono.just(response);
    }

    @PatchMapping("/api/v1/products/orders/canceled/{productId}/{productQuantity}")
    public void reduceProductState(@PathVariable UUID productId, @PathVariable Integer productQuantity) {
        productClientService.reduceProductState(productId, productQuantity);
    }

}
