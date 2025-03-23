package com.i4u.order.domain.entity;

public enum OrderStatus {

	PAID,              // 결제 완료
	SCHEDULED,         // 배송 예정
	SHIPPED,           // 배송 시작
	OUT_FOR_DELIVERY,  // 배송 중
	DELIVERED,         // 배송 완료
	ORDER_CANCELED,    // 주문 취소
	DELIVERY_CANCELED, // 배송 취소
	DELIVERY_FAILED,   // 배송 처리 실패
	DELIVERY_ERROR,    // 배송 오류
}
