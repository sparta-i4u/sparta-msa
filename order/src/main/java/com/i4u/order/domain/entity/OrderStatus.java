package com.i4u.order.domain.entity;

public enum OrderStatus {

	PAID,           // 결제 완료
	PROCESSING,     // 주문 처리 중
	SCHEDULED,      // 배송 예정
	SHIPPED,        // 배송 시작
	DELIVERED,      // 배송 완료
	CANCELED,       // 주문 취소

}
