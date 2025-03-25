package com.i4u.delivery.controller;

import brave.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i4u.DeliveryApplication;
import com.i4u.client.AuthClient;
import com.i4u.client.HubClient;
import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.dtos.request.DeliveryUpdateRequest;
import com.i4u.delivery.presentation.client.MessageClient;
import com.i4u.delivery.presentation.client.OrderClient;
import com.i4u.delivery.presentation.client.ProductClient;
import com.i4u.delivery.presentation.dtos.response.*;
import com.i4u.shipper.application.service.ShipperClient;
import com.i4u.shipper.presentation.dtos.request.MessageRequest;
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;
import com.jayway.jsonpath.JsonPath;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = DeliveryApplication.class)
@Transactional
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HubClient hubClient;

    @MockitoBean
    private AuthClient authClient;

    @MockitoBean
    private OrderClient orderClient;

    @MockitoBean
    private ShipperClient shipperClient;

    @MockitoBean
    private MessageClient messageClient;

    @MockitoBean
    private ProductClient productClient;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private UUID deliveryId;

    @Test
    @DisplayName("배송 생성 테스트 성공")
    void createDeliveryTest() throws Exception {
        // given
        DeliveryCreateRequest request = createDeliveryRequest();

        DeliveryHubCreateResponse responseHub = DeliveryHubCreateResponse.builder()
                .supplierHubId(request.getSupplierHubId()).recipientHubId(request.getRecipientHubId()).isDeleted(false).build();

        Mockito.when(hubClient.confirmHubsFromDelivery(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                        .thenReturn(responseHub);

        OrderProductResponse responseProduct = OrderProductResponse.builder()
                .isDeleted(false).productId(request.getProductId()).productName("책상")
                .productQuantity(request.getProductQuantity()).productTotalPrice(100000L).build();

        Mockito.when(productClient.confirmProduct(Mockito.any(UUID.class), Mockito.any(Integer.class)))
                        .thenReturn(responseProduct);

        ConfirmUserResponse responseUser = ConfirmUserResponse.builder()
                .isDeleted(false).userId(request.getRecipientId())
                .userRole("COMPANY_MANAGER").userSlackId("SLACK12334").email("company@company.com")
                .build();

        Mockito.when(authClient.confirmUser(Mockito.any(UUID.class)))
                        .thenReturn(responseUser);

        DeliveryShipperResponse responseShipper = DeliveryShipperResponse.builder()
                .recipientHubId(request.getRecipientHubId())
                .shipperId(UUID.randomUUID())
                .shipperEmail("shipper@shipper.email")
                .shipperSlackId("SLACKSLACK")
                .isDeleted(false)
                .build();

        Mockito.when(shipperClient.assignShipper(Mockito.any(UUID.class)))
                        .thenReturn(responseShipper);

        ResponseEntity<CommonResponse<MessageResDto>> responseMessage = ResponseEntity.ok(
                CommonResponse.success(MessageResDto.builder()
                                .messageId(UUID.randomUUID())
                                .messageContent("전송 내역")
                                .slackId(responseUser.getUserSlackId())
                        .build(),"")
        );

        Mockito.when(messageClient.sendInfoToMessage(Mockito.any(MessageRequest.class)))
                        .thenReturn(responseMessage);

        // then
        mockMvc.perform(
                        post("/api/v1/deliveries")
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "MASTER")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }


    @BeforeEach
    void setUpDelivery() throws Exception {
        // given
        DeliveryCreateRequest request = createDeliveryRequest();

        DeliveryHubCreateResponse responseHub = DeliveryHubCreateResponse.builder()
                .supplierHubId(request.getSupplierHubId()).recipientHubId(request.getRecipientHubId()).isDeleted(false).build();

        Mockito.when(hubClient.confirmHubsFromDelivery(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(responseHub);

        OrderProductResponse responseProduct = OrderProductResponse.builder()
                .isDeleted(false).productId(request.getProductId()).productName("책상")
                .productQuantity(request.getProductQuantity()).productTotalPrice(100000L).build();

        Mockito.when(productClient.confirmProduct(Mockito.any(UUID.class), Mockito.any(Integer.class)))
                .thenReturn(responseProduct);

        ConfirmUserResponse responseUser = ConfirmUserResponse.builder()
                .isDeleted(false).userId(request.getRecipientId())
                .userRole("COMPANY_MANAGER").userSlackId("SLACK12334").email("company@company.com")
                .build();

        Mockito.when(authClient.confirmUser(Mockito.any(UUID.class)))
                .thenReturn(responseUser);

        DeliveryShipperResponse responseShipper = DeliveryShipperResponse.builder()
                .recipientHubId(request.getRecipientHubId())
                .shipperId(UUID.randomUUID())
                .shipperEmail("shipper@shipper.email")
                .shipperSlackId("SLACKSLACK")
                .isDeleted(false)
                .build();

        Mockito.when(shipperClient.assignShipper(Mockito.any(UUID.class)))
                .thenReturn(responseShipper);

        ResponseEntity<CommonResponse<MessageResDto>> responseMessage = ResponseEntity.ok(
                CommonResponse.success(MessageResDto.builder()
                        .messageId(UUID.randomUUID())
                        .messageContent("전송 내역")
                        .slackId(responseUser.getUserSlackId())
                        .build(),"")
        );

        Mockito.when(messageClient.sendInfoToMessage(Mockito.any(MessageRequest.class)))
                .thenReturn(responseMessage);

        // then
        MvcResult result = mockMvc.perform(
                        post("/api/v1/deliveries")
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "MASTER")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();

        String deliveryIdString = JsonPath.read(result.getResponse().getContentAsString(), "$.deliveryId");
        this.deliveryId = UUID.fromString(deliveryIdString);

    }

    @Test
    @DisplayName("배송 단건 조회 성공")
    void getOneDeliveryTest() throws Exception {
        // when & then
        mockMvc.perform(
                        get("/api/v1/deliveries/" + deliveryId)
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "MASTER"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("배송 수정 실패 - 이미 배송 중")
    void updateDeliverySuccess() throws Exception {
        // given
        DeliveryUpdateRequest request = DeliveryUpdateRequest.builder()
                .address("부산시")
                .arriveHubId(UUID.randomUUID())
                .recipientId(UUID.randomUUID())
                .recipientSlackId("SLACKKCALS")
                .build();

        ResponseEntity<CommonResponse<DeliveryHubUpdateResponse>> responseHub =
                ResponseEntity.ok(CommonResponse.success(
                        DeliveryHubUpdateResponse.builder()
                                .arriveHubId(request.getArriveHubId())
                                .isDeleted(false).build()
                , "허브 내용"));

        Mockito.when(hubClient.updateConfirmHubsFromDelivery(Mockito.any(UUID.class)))
                        .thenReturn(responseHub);

        DeliveryShipperResponse responseShipper = DeliveryShipperResponse.builder()
                        .recipientHubId(request.getArriveHubId())
                                .shipperId(UUID.randomUUID()).shipperEmail("newShipper@email.com")
                        .shipperSlackId("SHIPPERSLACK").isDeleted(false).build();

        Mockito.when(shipperClient.assignShipper(Mockito.any(UUID.class)))
                .thenReturn(responseShipper);

        ConfirmUserResponse responseUser = ConfirmUserResponse.builder()
                .isDeleted(false).userId(request.getRecipientId())
                .userRole("COMPANY_MANAGER").userSlackId("SLACK12334").email("company@company.com")
                .build();

        Mockito.when(authClient.confirmUser(Mockito.any(UUID.class)))
                        .thenReturn(responseUser);

        // when & then
        mockMvc.perform(
                        put("/api/v1/deliveries/" + deliveryId)
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "COMPANY_MANAGER")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("배송 삭제 실패 - 이미 배송 중")
    void deleteDeliveryTest() throws Exception {
        // when & then
        mockMvc.perform(
                        delete("/api/v1/deliveries/" + deliveryId)
                                .header("X-User-Id", UUID.randomUUID().toString())
                                .header("X-User-Role", "MASTER"))
                .andExpect(status().isBadRequest());
    }

    private DeliveryCreateRequest createDeliveryRequest() {
        return DeliveryCreateRequest.builder()
                .orderId(UUID.randomUUID())
                .supplierHubId(UUID.randomUUID())
                .recipientHubId(UUID.randomUUID())
                .address("인천시 계양구")
                .requirement("3월 31일까지는 보내주셔야 합니다.")
                .productId(UUID.randomUUID())
                .productQuantity(1000)
                .recipientId(UUID.randomUUID())
                .build();
    }

}
