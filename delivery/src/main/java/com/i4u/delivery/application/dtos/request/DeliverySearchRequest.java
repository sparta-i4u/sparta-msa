package com.i4u.delivery.application.dtos.request;

import com.i4u.delivery.domain.entity.DeliveryState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliverySearchRequest {

    // 주문 ID
    private UUID orderId;

    // 현재 배송 상태
    private DeliveryState deliveryState;

    // 배송자 ID
    private UUID shipperId;

    // 수령자 ID
    private UUID recipientId;

    // 수령자 Slack ID
    private String recipientSlackId;

}
