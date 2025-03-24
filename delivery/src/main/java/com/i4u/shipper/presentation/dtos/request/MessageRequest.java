package com.i4u.shipper.presentation.dtos.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

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
