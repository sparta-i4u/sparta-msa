package com.i4u.delivery.presentation.controller;

import java.util.UUID;

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
import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.dtos.request.DeliverySearchRequest;
import com.i4u.delivery.application.dtos.request.DeliveryStatusUpdateRequest;
import com.i4u.delivery.application.dtos.request.DeliveryUpdateRequest;
import com.i4u.delivery.application.dtos.response.DeliveryCreateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetListResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetOneResponse;
import com.i4u.delivery.application.dtos.response.DeliveryStateUpdateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryUpdateResponse;
import com.i4u.delivery.application.service.DeliveryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {

	private final DeliveryService deliveryService;

	/**
	 * 배송 생성
	 *
	 * @param request : 생성할 배송 내용
	 * @return : 생성된 배송 내용
	 */ // MASTER (주문에서 생성 요청이 넘어오면 받아줄 포인트)
	@PostMapping
	public DeliveryCreateResponse createDelivery(@RequestBody DeliveryCreateRequest request) {
		System.out.println("supplierHubId: " + request.getSupplierHubId());
		System.out.println("recipientHubId: " + request.getRecipientHubId());
		DeliveryCreateResponse response = deliveryService.createDelivery(request);
		return response;
	}

	/**
	 * 배송 전체 조회 (+검색)
	 *
	 * @return : 조회한 전체 배송 내용
	 */  // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER
	@GetMapping
	public ResponseEntity<CommonResponse<PagedModel<DeliveryGetListResponse>>> getAllDeliveries(
		Pageable pageable, @ModelAttribute DeliverySearchRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		PagedModel<DeliveryGetListResponse> response = deliveryService.getAllDeliveries(pageable, request, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 전체 조회 성공"));
	}

	/**
	 * 배송 단건 조회
	 *
	 * @param deliveryId : 찾을 배송 ID
	 * @return : 찾은 배송 내용
	 */  // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 주문), COMPANY_MANAGER
	@GetMapping("/{deliveryId}")
	public ResponseEntity<CommonResponse<DeliveryGetOneResponse>> getOneDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		DeliveryGetOneResponse response = deliveryService.getOneDelivery(deliveryId, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 단건 조회 성공"));
	}

	/**
	 * 배송 수정
	 *
	 * @param deliveryId : 수정할 배송 ID
	 * @param request : 수정할 배송 내용
	 * @return : 수정한 배송 내용
	 */  // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 배송)
	@PutMapping("/{deliveryId}")
	public ResponseEntity<CommonResponse<DeliveryUpdateResponse>> updateDelivery(
		@PathVariable UUID deliveryId, @RequestBody DeliveryUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		DeliveryUpdateResponse response = deliveryService.updateDelivery(deliveryId, request, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 수정 성공"));
	}

	/**
	 * 배송 상태 수정
	 *
	 * @param deliveryId : 수정할 배송 ID
	 * @param request : 요청 내용
	 * @return : 수정한 배송 내용
	 */ // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 배송)
	@PatchMapping("/{deliveryId}")
	public ResponseEntity<CommonResponse<DeliveryStateUpdateResponse>> updateDeliveryState(
		@PathVariable UUID deliveryId, @RequestBody DeliveryStatusUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		DeliveryStateUpdateResponse response = deliveryService.updateDeliveryState(deliveryId, request, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 상태 수정 성공"));
	}

	/**
	 * 배송 삭제
	 * 
	 * @param deliveryId : 삭제할 배송 ID
	 * @return : 삭제한 배송 내용
	 */ // MASTER, HUB_MANAGER(담당 허브)
	@DeleteMapping("/{deliveryId}")
	public  ResponseEntity<CommonResponse> deleteDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		deliveryService.deleteDelivery(deliveryId, userId, role);
		return ResponseEntity.ok(CommonResponse.success(deliveryId, "배송 삭제 성공"));
	}


}
