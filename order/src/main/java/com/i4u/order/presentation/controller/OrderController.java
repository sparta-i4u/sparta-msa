package com.i4u.order.presentation.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.application.dtos.request.OrderCreateRequest;
import com.i4u.order.application.dtos.request.OrderSearchRequest;
import com.i4u.order.application.dtos.request.OrderStatusUpdateRequest;
import com.i4u.order.application.dtos.request.OrderUpdateRequest;
import com.i4u.order.application.dtos.response.OrderCreateResponse;
import com.i4u.order.application.dtos.response.OrderGetListResponse;
import com.i4u.order.application.dtos.response.OrderGetOneResponse;
import com.i4u.order.application.dtos.response.OrderStatusUpdateResponse;
import com.i4u.order.application.dtos.response.OrderUpdateResponse;
import com.i4u.order.application.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/v1/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	/**
	 * 주문 생성
	 *
	 * @param request : 생성할 주문의 정보
	 * @return : 생성된 주문 내용
	 */
	@PostMapping  // MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER (ALL) -> 검증 X
	public ResponseEntity<CommonResponse<OrderCreateResponse>> createOrder(
		@Valid @RequestBody OrderCreateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role){
		log.info("주문 생성 요청");
		OrderCreateResponse response = orderService.createOrder(request, userId);
		return ResponseEntity.ok(CommonResponse.success(response, "주문 생성 성공"));

	}

	@RabbitListener(queues = "${i4u.err.queue.order}")
	public void errOrder(Map<String, Object> errorMessage) {
		log.info("ERROR RECEIVE !");
		orderService.rollbackOrder(errorMessage);
	}

	/**
	 * 주문 전체 조회 (+검색)
	 *
	 * @return : 조회된 전체 주문 내용
	 */
	@GetMapping // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER(본인 주문) -> 허브 매니저만 소속 확인 필요
	public ResponseEntity<CommonResponse<PagedModel<OrderGetListResponse>>> getAllOrders(
		Pageable pageable, @ModelAttribute OrderSearchRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("주문 전체 조회 요청");
		PagedModel<OrderGetListResponse> orders = orderService.getAllOrders(pageable, request, userId, role);
		return ResponseEntity.ok(CommonResponse.success(orders, "주문 전체 조회 성공"));
	}

	/**
	 * 주문 단건 조회
	 *
	 * @param orderId : 조회할 주문의 ID
	 * @return : 조회된 주문 내용
	 */
	@GetMapping("/{orderId}")  // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER(본인 주문) -> 허브 매니저만 소속 확인 필요
	public ResponseEntity<CommonResponse<OrderGetOneResponse>> getOneOrder(
		@PathVariable UUID orderId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("주문 단건 조회 요청");
		OrderGetOneResponse response = orderService.getOneOrder(orderId, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "주문 단건 조회 성공"));
	}

	/**
	 * 주문 수정
	 *
	 * @param orderId : 수정할 주문의 ID
	 * @param request : 수정할 주문 정보
	 * @return : 수정된 주문 내용
	 */
	@PutMapping("/{orderId}")  // MASTER, HUB_MANAGER(담당 허브) -> 허브 매니저만 소속 확인 필요
	public ResponseEntity<CommonResponse<OrderUpdateResponse>> putOrder(
		@PathVariable UUID orderId, @Valid @RequestBody OrderUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("주문 수정 요청");
		OrderUpdateResponse response = orderService.updateOrder(orderId, request, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "주문 수정 성공"));
	}

	/**
	 * 주문 상태 수정
	 *
	 * @param orderId : 상태를 수정할 주문의 ID
	 * @param request : 수정할 주문 상태 정보
	 * @return : 상태가 수정된 주문 내용
	 */
	@PatchMapping("/{orderId}") // MASTER, HUB_MANAGER(담당 허브) -> 허브 매니저만 소속 확인 필요
	public ResponseEntity<CommonResponse<OrderStatusUpdateResponse>> patchOrder(
		@PathVariable UUID orderId, @RequestBody OrderStatusUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("주문 상태 수정 요청");
		OrderStatusUpdateResponse response = orderService.updateOrderStatus(orderId, request, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "주문 수정 성공"));
	}

	/**
	 * 주문 삭제
	 *
	 * @param orderId : 삭제할 주문의 ID
	 * @return : 삭제 완료된 주문 내용
	 */
	@DeleteMapping("/{orderId}") // MASTER, HUB_MANAGER(담당 허브) -> 허브 매니저만 소속 확인 필요
	public ResponseEntity<CommonResponse> deleteOrder(
		@PathVariable UUID orderId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("주문 삭제 요청");
		orderService.deleteOrder(orderId, userId, role);
		return ResponseEntity.ok(CommonResponse.success(orderId, "주문 삭제 성공"));
	}

}
