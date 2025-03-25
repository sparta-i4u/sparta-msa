package com.i4u.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4u.common.utils.CommonResponse;
import com.i4u.order.OrderApplication;
import com.i4u.order.application.dtos.request.OrderCreateRequest;
import com.i4u.order.application.dtos.request.OrderUpdateRequest;
import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.repository.OrderRepository;
import com.i4u.order.presentation.client.CompanyClient;
import com.i4u.order.presentation.client.DeliveryClient;
import com.i4u.order.presentation.client.HubClient;
import com.i4u.order.presentation.client.ProductClient;
import com.i4u.order.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.order.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.order.presentation.dtos.response.OrderCompanyResponse;
import com.i4u.order.presentation.dtos.response.OrderCompanyUpdateResponse;
import com.i4u.order.presentation.dtos.response.OrderProductResponse;
import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
// @AutoConfigureRestDocs
@SpringBootTest(classes = OrderApplication.class)
@Transactional
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CompanyClient companyClient;

    @MockitoBean
    private ProductClient productClient;

    @MockitoBean
    private HubClient hubClient;

    @MockitoBean
    private DeliveryClient deliveryClient;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private UUID orderId; // 전역 변수로 선언


    @Test
    @DisplayName("주문 생성 테스트 성공")
    void orderCreateTest() throws Exception{
        // given
        OrderCreateRequest request = createOrderRequest();
        OrderCompanyResponse responseCompany = createOrderCompanyResponse(request.getSupplierId(), request.getRecipientId());
        OrderProductResponse responseProduct = createOrderProductResponse(request.getProductId(), request.getProductQuantity());
        String deliveryQueue = "delivery.queue";
        OrderDeliveryRequest requestDelivery = createDeliveryRequest(request, responseCompany, responseProduct);
        Map<String, Object> response = createMapResponseFromDelivery(requestDelivery);

        // when
        Mockito.when(companyClient.confirmCompany(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(responseCompany);

        Mockito.when(productClient.confirmProduct(Mockito.any(UUID.class), Mockito.any(Integer.class)))
                        .thenReturn(responseProduct);

        Mockito.when(rabbitTemplate.convertSendAndReceive(deliveryQueue, request)).thenReturn(response);

        // then
        mockMvc.perform(
                        post("/api/v1/orders")
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "COMPANY_MANAGER")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @BeforeEach
    @DisplayName("주문 생성")
    void orderCreateSetUp() throws Exception{
        // given
        OrderCreateRequest request = createOrderRequest();
        OrderCompanyResponse responseCompany = createOrderCompanyResponse(request.getSupplierId(), request.getRecipientId());
        OrderProductResponse responseProduct = createOrderProductResponse(request.getProductId(), request.getProductQuantity());
        String deliveryQueue = "delivery.queue";
        OrderDeliveryRequest requestDelivery = createDeliveryRequest(request, responseCompany, responseProduct);
        Map<String, Object> response = createMapResponseFromDelivery(requestDelivery);

        Mockito.when(companyClient.confirmCompany(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(responseCompany);

        Mockito.when(productClient.confirmProduct(Mockito.any(UUID.class), Mockito.any(Integer.class)))
                .thenReturn(responseProduct);

        Mockito.when(rabbitTemplate.convertSendAndReceive(deliveryQueue, request)).thenReturn(response);

        MvcResult result = mockMvc.perform(
                        post("/api/v1/orders")
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "COMPANY_MANAGER")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();

        // jsonPath로 data.orderId 추출하여 전역 변수에 저장
        this.orderId = UUID.fromString(
                JsonPath.read(result.getResponse().getContentAsString(), "$.data.orderId")
        );
    }


    @Test
    @DisplayName("주문 단건 조회 성공")
    void getOneOrderTest() throws Exception {
        // when & then
        mockMvc.perform(
                        get("/api/v1/orders/" + orderId)
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "MASTER"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 수정 실패 - 이미 배송 시작")
    void updateOrderTest() throws Exception {
        // given
        OrderUpdateRequest request = createOrderUpdateRequest();

        CommonResponse<OrderCompanyUpdateResponse> responseCompany = makeCommonResponseOrderCompanyUpdateResponse();

        Mockito.when(companyClient.confirmCompanyUpdate(Mockito.any(UUID.class)))  // companyClient의 confirmCompany를 mock
                .thenReturn(ResponseEntity.ok(responseCompany));  // ResponseEntity.ok로 감싸서 반환

        Map<String, Object> productResponse = makeProductResponse(request);

        Mockito.when(productClient.confirmProductUpdate(Mockito.any(UUID.class), Mockito.any(Integer.class), Mockito.any(UUID.class), Mockito.any(Integer.class)))
                        .thenReturn(productResponse);

        Mockito.when(deliveryClient.updateDeliveryByOrder(Mockito.any(OrderDeliveryUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok(CommonResponse.success("", ""))); // mock으로 성공적인 응답을 반환

        // when & then
        mockMvc.perform(
                        put("/api/v1/orders/" + orderId)
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "MASTER")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("주문 삭제 성공")
    void deleteOrderTest() throws Exception {
        // when & then
        mockMvc.perform(
                        delete("/api/v1/orders/" + orderId)
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "MASTER"))
                .andExpect(status().isOk());
    }

    private OrderUpdateRequest createOrderUpdateRequest() {
        return OrderUpdateRequest.builder()
                .supplierId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .productQuantity(1500)
                .requirement("3월 30일까지 꼭! 보내주셔야 합니다")
                .build();
    }
    
    private OrderCreateRequest createOrderRequest() {
        return OrderCreateRequest.builder()
                .supplierId(UUID.randomUUID())
                .recipientId(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .productQuantity(1000)
                .requirement("3월 30일까지 보내주세요")
                .build();
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

    private CommonResponse<OrderCompanyUpdateResponse> makeCommonResponseOrderCompanyUpdateResponse () {
        UUID supplierId = UUID.randomUUID();
        UUID supplierHubId = UUID.randomUUID();
        boolean isDeleted = false;

        OrderCompanyUpdateResponse response = OrderCompanyUpdateResponse.builder()
                .isDeleted(isDeleted)
                .supplierId(supplierId)
                .supplierHubId(supplierHubId)
                .build();

        CommonResponse<OrderCompanyUpdateResponse> responseCompany = CommonResponse.success(response, "업체 검증 완료");

        return responseCompany;
    }

    private Map<String, Object> makeProductResponse(OrderUpdateRequest request) {
        Map<String, Object> productResponse = new HashMap<>();
        productResponse.put("productId", request.getProductId());
        productResponse.put("productQuantity", request.getProductQuantity());
        productResponse.put("productTotalPrice", 100000L);
        productResponse.put("isDeleted", false);
        return productResponse;
    }

}
