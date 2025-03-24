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
        System.out.println("상품이 없나요 ? " + product.getId());

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
        System.out.println("재고가 없나요 ? " + product.getCount() + " , 주문한 수량 : " + productQuantity);
        System.out.println(isStockInsufficient);

        // 재고가 충분하면 (false) 재고 감소
        if (!isStockInsufficient) {
            product.decreaseCount(productQuantity);
        }

        return OrderProductResponse.builder()
            .isDeleted(isStockInsufficient)
            .productId(product.getId())
            .productName(product.getName())
            .productQuantity(isStockInsufficient ? 0 : productQuantity)
            .productTotalPrice(isStockInsufficient ? 0L : (long) (product.getPrice() * productQuantity))
            .build();
    }



    // 중간에 주문 변경 요청이 들어왔을 때, 상품을 검증하는 메서드
    public Map<String, Object> confirmProductUpdate(UUID beforeProductId, Integer beforeProductQuantity, UUID afterProductId, Integer afterProductQuantity) {
        // 처음 주문했던 상품의 재고는 증가시킴 (예외 없이 실행)
        Product beforeProduct = productRepository.findById(beforeProductId)
            .orElse(null);
        if (beforeProduct != null) {
            beforeProduct.increaseCount(beforeProductQuantity);
        }

        // 변경할 상품 조회
        Product afterProduct = productRepository.findById(afterProductId).orElse(null);

        // isDeleted 조건 추가
        boolean isDeleted = (afterProduct == null || afterProduct.getCount() < afterProductQuantity);

        if (!isDeleted) {
            // 이후에 주문한 상품의 재고 감소
            afterProduct.decreaseCount(afterProductQuantity);
        }

        Map<String, Object> productResponse = new HashMap<>();
        productResponse.put("productId", afterProductId);
        productResponse.put("productQuantity", afterProductQuantity);
        productResponse.put("productTotalPrice", isDeleted ? 0 : afterProductQuantity * afterProduct.getPrice());
        productResponse.put("isDeleted", isDeleted);

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
