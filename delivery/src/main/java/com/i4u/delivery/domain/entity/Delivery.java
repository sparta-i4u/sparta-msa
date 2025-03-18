package com.i4u.delivery.domain.entity;

import java.util.Optional;
import java.util.UUID;

import com.i4u.common.entity.Basic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_delivery")
public class Delivery extends Basic {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID deliveryId;

	@Column(name = "order_id")
	private UUID orderId;

	@Column(name = "delivery_state")
	private DeliveryState deliveryState;

	@Column
	private UUID departHubId;

	@Column
	private UUID arriveHubId;

	@Column
	private String address;

	@Column(name = "recipient_id")
	private UUID recipientId;

	@Column(name = "recipient_slack_id")
	private String recipientSlackId;

	@Column(name = "shipper_id")
	private UUID shipperId;

	/**
	 * 배송 수정
	 *
	 * @param updatingDelivery : 수정할 배송 내용
	 */
	public void updateDelivery(Delivery updatingDelivery) {
		Optional.ofNullable(updatingDelivery.getAddress()).ifPresent(address -> this.address = address);
		Optional.ofNullable(updatingDelivery.getRecipientId()).ifPresent(recipientId -> this.recipientId = recipientId);
		Optional.ofNullable(updatingDelivery.getRecipientSlackId()).ifPresent(recipientSlackId -> this.recipientSlackId = recipientSlackId);
	}

	/**
	 * 배송 상태 수정
	 *
	 * @param updatingDelivery : 상태를 수정할 배송 내용
	 */
	public void updateDeliveryState(Delivery updatingDelivery) {
		Optional.ofNullable(updatingDelivery.getDeliveryState()).ifPresent(deliveryState -> this.deliveryState = deliveryState);
	}

	public void updateDeliveryStateByOrder(DeliveryState deliveryState) {
		this.deliveryState = deliveryState;
	}
}
