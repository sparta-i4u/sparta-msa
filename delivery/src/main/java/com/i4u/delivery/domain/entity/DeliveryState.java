package com.i4u.delivery.domain.entity;

public enum DeliveryState {
	
	// 배송 대기
	PENDING,
	// 상품 준비 중
	PREPARING,
	// 출고 준비 완료
	READY_TO_SHIP,
	// 배송 시작
	SHIPPED,
	// 배송 중
	OUT_FOR_DELIVERY,
	// 주문 취소
	ORDER_CANCELED,
	// 배송 취소
	DELIVERY_CANCELED,
	// 삭제
	DELETED

}