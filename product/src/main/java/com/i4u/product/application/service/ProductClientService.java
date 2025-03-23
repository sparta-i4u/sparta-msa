package com.i4u.product.application.service;

import com.i4u.product.application.dto.response.OrderProductResponse;
import com.i4u.product.domain.Product;
import com.i4u.product.domain.repository.ProductQueryRepository;
import com.i4u.product.domain.repository.ProductRepository;
import com.i4u.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductClientService {

    private final ProductRepository productRepository;

    // 초반 요청이 들어왔을 때, 상품을 검증하는 메서드
    public OrderProductResponse confirmProduct(UUID productId, Integer productQuantity) {
        // 상품 조회
        Product product = productRepository.findById(productId).orElse(null);

        // 상품이 없으면 isDeleted = true로 반환
        if (product == null) {
            return OrderProductResponse.builder()
                .isDeleted(true)
                .productId(productId)
                .productName("Unknown")
                .productQuantity(0)
                .productTotalPrice(0L)
                .build();
        }

        // 재고 부족 여부 확인 (true면 재고가 부족한거임)
        boolean isStockInsufficient = product.getCount() < productQuantity;

        // 재고가 충분하면 (false) 재고 감소
        if (!isStockInsufficient) {
            product.decreaseCount(productQuantity);
        }

        return OrderProductResponse.builder()
            .isDeleted(!isStockInsufficient)
            .productId(product.getId())
            .productName(product.getName())
            .productQuantity(isStockInsufficient ? 0 : productQuantity)
            .productTotalPrice(isStockInsufficient ? 0L : (long) (product.getPrice() * productQuantity))
            .build();
    }



    // 중간에 주문 변경 요청이 들어왔을 때, 상품을 검증하는 메서드
    public Map<String, Object> confirmProductUpdate(UUID beforeProductId, Integer beforeProductQuantity, UUID afterProductId, Integer afterProductQuantity) {
        Product beforeProduct = productRepository.findById(beforeProductId).orElseThrow(
                () -> new ProductNotFoundException("해당 상품이 존재하지 않습니다.")
        );

        Product afterProduct = productRepository.findById(afterProductId).orElseThrow(
                () -> new ProductNotFoundException("해당 상품이 존재하지 않습니다.")
        );

        // 처음 주문했던 상품의 재고는 증가시킴
        beforeProduct.increaseCount(beforeProductQuantity);

        // 이후에 주문한 상품의 재고는 감소시킴
        if (afterProduct.getCount() < afterProductQuantity) {
            throw new IllegalArgumentException("해당 상품의 재고가 없습니다.");
        }

        afterProduct.decreaseCount(afterProductQuantity);

        Map<String, Object> productResponse = new HashMap<>();
        productResponse.put("productId", afterProductId);
        productResponse.put("productQuantity", afterProductQuantity);
        productResponse.put("productTotalPrice", afterProductQuantity * afterProduct.getPrice());

        return productResponse;
    }

    public void reduceProductState(UUID productId, Integer productQuantity) {
        // 상품의 재고를 증가시키는 로직 (주문이 취소됨)
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당 상품이 존재하지 않습니다.")
        );

        product.increaseCount(productQuantity);
    }
}
