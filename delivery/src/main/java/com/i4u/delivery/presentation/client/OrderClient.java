package com.i4u.delivery.presentation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.i4u.delivery.presentation.dtos.request.DeliveryOrderCreateRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderDeleteRequest;
import com.i4u.delivery.presentation.dtos.request.DeliveryOrderStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.response.DeliveryOrderCreateResponse;

@FeignClient(name = "order")
public interface OrderClient {

	// 배송이 생성되면 바로 order 측으로 요청 전송 (근데 응답은 필요 없지 않나 ..? -> 반환 타입 고민해보기)
	@GetMapping("/deliveries/{deliveryId}/orders")
	DeliveryOrderCreateResponse notificationDeliveryCreate(DeliveryOrderCreateRequest request);

	// 주문 측에서는 이 API를 받으면 주문 상태를 배송 취소/삭제 등으로 변경하기
	@GetMapping("/deliveries/{deliveryId}/orders/noti-deleted")
	void notificationDeliveryCanceled(DeliveryOrderDeleteRequest build);

	@GetMapping("/deliveries/{deliveryId}/orders/noti-update")
	void notificationDeliveryState(DeliveryOrderStateUpdateRequest build);
}
