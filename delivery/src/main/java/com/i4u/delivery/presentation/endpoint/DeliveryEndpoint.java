package com.i4u.delivery.presentation.endpoint;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.service.DeliveryClientService;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryStateUpdateRequest;
import com.i4u.delivery.presentation.dtos.request.OrderDeliveryUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryEndpoint {

	private final DeliveryClientService deliveryClientService;

	/**
	 * 배송 생성
	 *
	 * @param request : 생성할 배송 내용
	 * @return : 생성된 배송 내용
	 */ // MASTER (주문에서 생성 요청이 넘어오면 받아줄 포인트)
	@RabbitListener(queues = "${i4u.queue.delivery}")
	public Map<String, Object> createDelivery(DeliveryCreateRequest request) {
		Map<String, Object> response = deliveryClientService.createDelivery(request);
		return response;
	}

	@PutMapping("/update-byorder")
	ResponseEntity<CommonResponse> updateDeliveryByOrder(@RequestBody OrderDeliveryUpdateRequest request){
		deliveryClientService.updateDeliveryByOrder(request);
		return ResponseEntity.ok(CommonResponse.success("", "배송 수정 성공"));
	}

	@PatchMapping("/updatestate-byorder")
	ResponseEntity<CommonResponse> updateDeliveryState(@RequestBody OrderDeliveryStateUpdateRequest orderCanceled) {
		deliveryClientService.updateDeliveryStateByOrder(orderCanceled);
		return ResponseEntity.ok(CommonResponse.success("", "배송 상태 수정 성공"));
	}

}
