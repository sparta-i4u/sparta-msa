package com.i4u.delivery.application.service;

import org.springframework.stereotype.Service;

import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;
import com.i4u.delivery.domain.repository.DeliveryRepository;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.OrderDeliveryResponse;
import com.i4u.delivery.presentation.dtos.response.OrderDeliveryUpdateResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryClientService {

	private final DeliveryRepository deliveryRepository;

	public OrderDeliveryResponse createDelivery(OrderDeliveryRequest request) {
		// [hubClient] 검증 필요
		// 허브가 두개라 각각 검증 필요


		// [userClient] 검증 필요
		// user 검증 요청 보내면서 slackId 받아오기

		// [shipperClient] 배정 필요
		return null;
	}

	public OrderDeliveryUpdateResponse updateDelivery(OrderDeliveryUpdateRequest request) {
		// [hubClient] 검증 필요
		
		// [shipperClient] 재배정 필요
		return null;
	}

	public void updateDeliveryState(OrderDeliveryStateUpdateRequest orderCanceled) {
		// 주문이 취소된 경우
		// 1. 받아온 배송 ID로 배송 검색
		Delivery delivery = deliveryRepository.findById(orderCanceled.getDeliveryId())
			.filter(o -> o.getOrderId().equals(orderCanceled.getOrderId()))
			.orElseThrow(() -> new IllegalArgumentException("해당 배송이 존재하지 않습니다."));

		// 2. 배송 상태 업데이트
		delivery.updateDeliveryStateByOrder(DeliveryState.ORDER_CANCELED);

		// 3. 배송 담당자가 있다면 함께 알려야 할까 ?
	}
}
