package com.i4u.message.application.dto;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIMessageReqDto {

    private UUID orderId;
    private String recipientEmail;
    private String recipientSlackId;
    private String productName;
    private Integer productQuantity;
    private String requirement;
    private UUID supplierHubId;
    private UUID recipientHubId;
    private String shipperEmail;
    private String shipperSlackId;
    private String address;

}
