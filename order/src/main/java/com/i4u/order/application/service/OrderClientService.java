package com.i4u.order.application.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.i4u.order.application.exception.OrderException;
import com.i4u.order.domain.entity.Order;
import com.i4u.order.domain.entity.OrderStatus;
import com.i4u.order.domain.repository.OrderRepository;
import com.i4u.order.presentation.dtos.request.DeliveryOrderStateUpdateRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderClientService {

	private final OrderRepository orderRepository;

	/**
	 * 배송 상태 변경에 따른 주문 상태 변경
	 *
	 * @param request : 변경할 주문 내용
	 */
	@Transactional
	public void notificationDeliveryState(DeliveryOrderStateUpdateRequest request) {
		Order order = orderRepository.findById(request.getOrderId()).filter(o -> !o.getIsDeleted())
			.orElseThrow(() -> new OrderException("해당 주문 내역이 없습니다.", HttpStatus.BAD_REQUEST));

		order.updateOrderStateFromDelivery(request.getDeliveryId(), switchIntoOrderStatus(request.getDeliveryState()));
	}

	/**
	 * 배송 상태에 따른 주문 상태 수정
	 *
	 * @param deliveryStatus : 배송 상태
	 * @return : 올바른 주문 상태로 반환
	 */
	private OrderStatus switchIntoOrderStatus(String deliveryStatus) {
		switch (deliveryStatus) {
			case "SHIPPED" :
				return OrderStatus.SHIPPED;
			case "OUT_FOR_DELIVERY" :
				return OrderStatus.OUT_FOR_DELIVERY;
			case "DELIVERED" :
				return OrderStatus.DELIVERED;
			case "DELIVERY_CANCELED" :
			case "DELETED" :
				return OrderStatus.DELIVERY_CANCELED;
			default :
				return OrderStatus.SCHEDULED;
		}
	}

}
