package com.i4u.delivery.application.service;

import org.springframework.stereotype.Service;

import com.i4u.delivery.domain.entity.Delivery;
import com.i4u.delivery.domain.entity.DeliveryState;
import com.i4u.delivery.domain.repository.DeliveryRepository;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryUpdateRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryClientService {

	private final DeliveryRepository deliveryRepository;

	/**
	 * 주문 수정에 따른 배송 수정
	 * 
	 * @param request : 수정 내용
	 */
	@Transactional
	public void updateDeliveryByOrder(OrderDeliveryUpdateRequest request) {
		// 1. 주문 ID에 해당하는 배송 검색
		Delivery delivery = deliveryRepository.findByOrderId(request.getOrderId())
			.orElseThrow(() -> new IllegalArgumentException("해당 배송은 없습니다."));

		// 2. [hubClient] 검증 필요
		if (request.getSupplierHubId().equals(delivery.getDepartHubId())) {
			// hub가 같으니까 바꿀 필요가 없음
			throw new IllegalArgumentException("허브가 변경되지 않았습니다. ");
		}
		// hubClient 요청 보내기
		
		// 3. [shipperClient] 재배정 필요
		// shipperClient 호출해서 재배정 받고, shipperId 변경하기

		// 4. 배송 내역이 수정되었으므로 배송 상태도 수정 (출고 준비 중으로 변경)
		delivery.updateDeliveryStateByOrder(DeliveryState.PREPARING);
	}

	/**
	 * 주문 취소에 의한 배송 취소
	 * 
	 * @param orderCanceled : 주문이 취소된 주문 내역
	 */
	@Transactional
	public void updateDeliveryStateByOrder(OrderDeliveryStateUpdateRequest orderCanceled) {
		// 주문이 취소된 경우
		// 1. 받아온 배송 ID로 배송 검색
		Delivery delivery = deliveryRepository.findById(orderCanceled.getDeliveryId())
			.filter(o -> o.getOrderId().equals(orderCanceled.getOrderId()))
			.orElseThrow(() -> new IllegalArgumentException("해당 배송이 존재하지 않습니다."));

		// 2. 배송 상태 업데이트
		delivery.updateDeliveryStateByOrder(DeliveryState.ORDER_CANCELED);
	}
}
