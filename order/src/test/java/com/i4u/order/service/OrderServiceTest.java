package com.i4u.order.service;

import com.i4u.order.application.dtos.request.OrderCreateRequest;
import com.i4u.order.application.service.OrderService;
import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;
import com.i4u.order.domain.repository.OrderRepository;
import com.i4u.order.presentation.client.CompanyClient;
import com.i4u.order.presentation.client.DeliveryClient;
import com.i4u.order.presentation.client.HubClient;
import com.i4u.order.presentation.client.ProductClient;
import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.any;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CompanyClient companyClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private HubClient hubClient;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(orderRepository, companyClient, productClient, deliveryClient, hubClient, rabbitTemplate);
    }

    @Test
    @DisplayName("주문 생성 테스트")
    void createOrderTest() throws Exception {
        // given
        Order order = makeOrder();
        OrderCreateRequest request = OrderCreateRequest.builder()
                .supplierId(order.getSupplierId())
                .recipientId(order.getRecipientId())
                .productId(order.getProductId())
                .productQuantity(order.getProductQuantity())
                .requirement(order.getRequirement())
                .build();

        // when
        OrderCompanyResponse responseCompany = createOrderCompanyResponse(request.getSupplierId(), request.getRecipientId());
        OrderProductResponse responseProduct = createOrderProductResponse(request.getProductId(), request.getProductQuantity());
        String deliveryQueue = "delivery.queue";
        OrderDeliveryRequest requestDelivery = createDeliveryRequest(request, responseCompany, responseProduct);
        Map<String, Object> response = createMapResponseFromDelivery(requestDelivery);

        Mockito.lenient().when(companyClient.confirmCompany(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(responseCompany);

        Mockito.lenient().when(productClient.confirmProduct(Mockito.any(UUID.class), Mockito.any(Integer.class)))
                .thenReturn(responseProduct);

        Mockito.lenient().when(rabbitTemplate.convertSendAndReceive(deliveryQueue, request)).thenReturn(response);
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);

        // then
        orderService.createOrder(request, order.getUserId());
    }


    private OrderCompanyResponse createOrderCompanyResponse(UUID supplierId, UUID recipientId) {
        return OrderCompanyResponse.builder()
                .isDeleted(false)
                .supplierHubId(UUID.randomUUID())
                .supplierId(supplierId)
                .recipientHubId(UUID.randomUUID())
                .recipientId(recipientId)
                .address("업체 주소")
                .build();
    }

    private OrderProductResponse createOrderProductResponse(UUID productId, Integer productQuantity) {
        return OrderProductResponse.builder()
                .isDeleted(false)
                .productId(productId)
                .productQuantity(productQuantity)
                .productName("목재 책상")
                .productTotalPrice(1000000L)
                .build();
    }

    private OrderDeliveryRequest createDeliveryRequest(OrderCreateRequest request, OrderCompanyResponse responseCompany, OrderProductResponse responseProduct) {
        return OrderDeliveryRequest.builder()
                .orderId(UUID.randomUUID())
                .recipientHubId(responseCompany.getRecipientHubId())
                .supplierHubId(responseCompany.getSupplierHubId())
                .address(responseCompany.getAddress())
                .requirement(request.getRequirement())
                .recipientId(request.getRecipientId())
                .productId(request.getProductId())
                .productName(responseProduct.getProductName())
                .productQuantity(responseProduct.getProductQuantity())
                .build();

    }

    private Map<String, Object> createMapResponseFromDelivery (OrderDeliveryRequest request){
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", request.getOrderId());
        response.put("deliveryState", "SHIPPED");
        response.put("deliveryId", UUID.randomUUID());

        return response;
    }


    private Order makeOrder() {
        return Order.builder()
                .orderId(UUID.randomUUID())
                .supplierId(UUID.randomUUID())
                .supplierHubId(UUID.randomUUID())
                .recipientId(UUID.randomUUID())
                .recipientHubId(UUID.randomUUID())
                .requirement("4월 1일. 반드시 부탁드립니다.")
                .productId(UUID.randomUUID())
                .productQuantity(1000)
                .productTotalPrice(10000L)
                .userId(UUID.randomUUID())
                .deliveryId(UUID.randomUUID())
                .orderStatus(OrderStatus.PAID)
                .build();


    }
}
