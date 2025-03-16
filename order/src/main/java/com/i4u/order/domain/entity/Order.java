package com.i4u.order.domain.entity;

import java.util.Optional;
import java.util.UUID;

import com.i4u.common.entity.Basic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends Basic {

	// 주문 ID
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	public UUID orderId;

	// 요청 업체 ID (companyId)
	@Column(name = "supplier_id")
	public UUID supplierId;

	// 수령 업체 ID (companyId)
	@Column(name = "recipient_id")
	public UUID recipientId;

	// 주문할 상품의 ID
	@Column(name = "product_id")
	public UUID productId;

	// 주문할 상품의 수량
	@Column(name = "product_quantity")
	public Integer productQuantity;

	// 요청 사항
	@Column(name = "requirement")
	public String requirement;

	// 배송 주소
	@Column(name = "address")
	public String address;

	// 배송 ID
	@Column(name = "delivery_id")
	public UUID deliveryId;

	// 주문 상태
	@Column(name = "order_status")
	public OrderStatus orderStatus;

	/**
	 * 주문 수정
	 *
	 * @param updateOrder : 수정할 주문 내용
	 */
	public void updateOrder(Order updateOrder) {
		Optional.ofNullable(updateOrder.getRecipientId()).ifPresent(recipientId -> this.recipientId = recipientId);
		Optional.ofNullable(updateOrder.getProductId()).ifPresent(productId -> this.productId = productId);
		Optional.ofNullable(updateOrder.getProductQuantity()).ifPresent(productQuantity -> this.productQuantity = productQuantity);
		Optional.ofNullable(updateOrder.getRequirement()).ifPresent(requirement -> this.requirement = requirement);
		Optional.ofNullable(updateOrder.getAddress()).ifPresent(address -> this.address = address);
	}

	/**
	 * 주문 상태 변경
	 * 
	 * @param updateOrder : 상태를 변경할 주문 내용
	 */
	public void updateOrderState(Order updateOrder) {
		Optional.ofNullable(updateOrder.getOrderStatus()).ifPresent(orderStatus -> this.orderStatus = orderStatus);
	}
}
