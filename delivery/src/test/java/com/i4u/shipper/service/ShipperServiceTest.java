package com.i4u.shipper.service;

import com.i4u.client.AuthClient;
import com.i4u.client.HubClient;
import com.i4u.shipper.application.dtos.request.ShipperCreateRequest;
import com.i4u.shipper.application.service.ShipperService;
import com.i4u.shipper.domain.entity.Shipper;
import com.i4u.shipper.domain.entity.ShipperType;
import com.i4u.shipper.domain.repository.ShipperRepository;
import com.i4u.shipper.presentation.dtos.response.ConfirmUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ShipperServiceTest {

    private ShipperService shipperService;

    @Mock
    private ShipperRepository shipperRepository;

    @Mock
    private HubClient hubClient;

    @Mock
    private AuthClient authClient;

    @BeforeEach
    void setUp() {
        this.shipperService = new ShipperService(shipperRepository, hubClient, authClient);
    }

    @Test
    @DisplayName("배송 담당자 생성 성공")
    void createShipperTest() throws Exception {
        // given
        UUID masterId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Shipper shipper = makeShipper(userId);
        ShipperCreateRequest request = ShipperCreateRequest.builder()
                .hubId(shipper.getHubId())
                .userId(userId)
                .shipperType(ShipperType.COMPANY)
                .build();

        // when
        Mockito.lenient().when(hubClient.confirmHubFromUser(Mockito.any(UUID.class))).thenReturn(shipper.getHubId());
        Mockito.lenient().when(authClient.confirmUser(Mockito.any(UUID.class))).thenReturn(createFeignClientUserResponse(userId));
        Mockito.lenient().when(shipperRepository.confirmShipperOrder(Mockito.any(UUID.class))).thenReturn(1);
        Mockito.lenient().when(shipperRepository.save(Mockito.any(Shipper.class))).thenReturn(shipper);

        // then
        shipperService.createShipper(request, masterId, "MASTER");
    }

    ConfirmUserResponse createFeignClientUserResponse(UUID userId) {
        return ConfirmUserResponse.builder()
                .userId(userId).userRole("DELIVERY")
                .userSlackId("SDFESFLKJ").isDeleted(false).build();
    }

    private Shipper makeShipper(UUID userId) {
        return Shipper.builder()
                .shipperId(userId)
                .shipperOrder(1)
                .shipperType(ShipperType.COMPANY)
                .userEmail("shipper@mea.dml")
                .userId(userId)
                .hubId(UUID.randomUUID())
                .build();
    }

}
