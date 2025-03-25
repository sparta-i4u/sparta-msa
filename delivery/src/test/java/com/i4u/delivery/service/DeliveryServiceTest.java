package com.i4u.delivery.service;

import com.i4u.client.AuthClient;
import com.i4u.client.HubClient;
import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.dtos.response.DeliveryCreateResponse;
import com.i4u.delivery.application.service.DeliveryService;
import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;
import com.i4u.delivery.domain.repository.DeliveryRepository;
import com.i4u.delivery.presentation.client.MessageClient;
import com.i4u.delivery.presentation.client.OrderClient;
import com.i4u.delivery.presentation.client.ProductClient;
import com.i4u.delivery.presentation.dtos.response.DeliveryHubCreateResponse;
import com.i4u.delivery.presentation.dtos.response.DeliveryShipperResponse;
import com.i4u.delivery.presentation.dtos.response.MessageResDto;
import com.i4u.delivery.presentation.dtos.response.OrderProductResponse;
import com.i4u.shipper.application.service.ShipperClient;
import com.i4u.shipper.presentation.dtos.request.MessageRequest;
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {

    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private HubClient hubClient;

    @Mock
    private AuthClient authClient;

    @Mock
    private OrderClient orderClient;

    @Mock
    private ShipperClient shipperClient;

    @Mock
    private MessageClient messageClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @BeforeEach
    void setUp () {
        this.deliveryService = new DeliveryService(deliveryRepository, hubClient, authClient, orderClient, shipperClient, messageClient, productClient, rabbitTemplate);
    }

    @Test
    @DisplayName("배송 생성 테스트")
    void createDeliveryTest() throws Exception {
        // given
        Delivery delivery = makeDelivery();
        DeliveryCreateRequest request = DeliveryCreateRequest.builder()
                .orderId(delivery.getOrderId())
                .supplierHubId(delivery.getDepartHubId())
                .recipientHubId(delivery.getArriveHubId())
                .address(delivery.getAddress())
                .requirement("4월 1일까지는 보내주세요.")
                .productId(UUID.randomUUID())
                .productName("목재 책상")
                .productQuantity(1000)
                .recipientId(UUID.randomUUID())
                .build();

        // when
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

        Mockito.lenient().when(messageClient.sendInfoToMessage(Mockito.any(MessageRequest.class)))
                .thenReturn(responseMessage);

        DeliveryCreateResponse response = deliveryService.createDelivery(request);

        Mockito.lenient().when(deliveryRepository.save(Mockito.any(Delivery.class)))
                .thenReturn(delivery);

        // then
//        System.out.println(response.getDeliveryId());
    }


    private Delivery makeDelivery() {
        return Delivery.builder()
                .deliveryId(UUID.randomUUID())
                .orderId(UUID.randomUUID())
                .deliveryState(DeliveryState.PREPARING)
                .departHubId(UUID.randomUUID())
                .arriveHubId(UUID.randomUUID())
                .address("인천시 계양구")
                .recipientId(UUID.randomUUID())
                .recipientSlackId("RECIPIENT")
                .shipperId(UUID.randomUUID())
                .build();
    }
}
