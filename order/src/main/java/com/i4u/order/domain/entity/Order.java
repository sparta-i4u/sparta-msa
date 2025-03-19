package com.i4u.order.domain.entity;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

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

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_order")
@SQLRestriction("is_deleted IS NULL OR is_deleted = false")
public class Order extends Basic {

	// 주문 ID
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID orderId;

	// 요청 업체 ID (companyId)
	@Column(name = "supplier_id")
	private UUID supplierId;

	// 수령 업체 ID (companyId)
	@Column(name = "recipient_id")
	private UUID recipientId;

	// 주문할 상품의 ID
	@Column(name = "product_id")
	private UUID productId;

	// 주문할 상품의 수량
	@Column(name = "product_quantity")
	private Integer productQuantity;

	// 요청 사항
	@Column(name = "requirement")
	private String requirement;

	// 상품 총 가격
	@Column(name = "product_total_price")
	private Long productTotalPrice;
	
	// 주문한 사용자
	@Column(name = "user_id")
	private UUID userId;

	// 배송 ID
	@Column(name = "delivery_id")
	private UUID deliveryId;

	// 주문 상태
	@Column(name = "order_status")
	private OrderStatus orderStatus;

	/**
	 * 주문 수정
	 *
	 * @param updateOrder : 수정할 주문 내용
	 */
	public void updateOrder(Order updateOrder) {
		Optional.ofNullable(updateOrder.getSupplierId()).ifPresent(supplierId -> this.supplierId = supplierId);
		Optional.ofNullable(updateOrder.getProductId()).ifPresent(productId -> this.productId = productId);
		Optional.ofNullable(updateOrder.getProductQuantity()).ifPresent(productQuantity -> this.productQuantity = productQuantity);
		Optional.ofNullable(updateOrder.getRequirement()).ifPresent(requirement -> this.requirement = requirement);
	}

	/**
	 * 주문 상태 변경
	 * 
	 * @param updateOrder : 상태를 변경할 주문 내용
	 */
	public void updateOrderState(Order updateOrder) {
		Optional.ofNullable(updateOrder.getOrderStatus()).ifPresent(orderStatus -> this.orderStatus = orderStatus);
	}

	public void updateOrderStateFromDelivery(UUID deliveryId, OrderStatus orderStatus) {
		this.deliveryId = deliveryId;
		this.orderStatus = orderStatus;
	}
}
