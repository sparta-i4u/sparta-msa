package com.i4u.delivery.presentation.controller;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

import com.i4u.delivery.application.dtos.request.DeliverySearchRequest;

import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.i4u.common.utils.CommonResponse;
import com.i4u.delivery.application.dtos.request.DeliveryCreateRequest;
import com.i4u.delivery.application.dtos.request.DeliveryStatusUpdateRequest;
import com.i4u.delivery.application.dtos.request.DeliveryUpdateRequest;
import com.i4u.delivery.application.dtos.response.DeliveryCreateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetOneResponse;
import com.i4u.delivery.application.dtos.response.DeliveryGetListResponse;
import com.i4u.delivery.application.dtos.response.DeliveryStateUpdateResponse;
import com.i4u.delivery.application.dtos.response.DeliveryUpdateResponse;
import com.i4u.delivery.application.service.DeliveryService;

import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

	// Controller가 아니라 Endpoint로 생각해야할까 ?
	// TODO : 사용자 정보 받는 로직 추가

	private final DeliveryService deliveryService;

	/**
	 * 배송 생성
	 *
	 * @param request : 생성할 배송 내용
	 * @return : 생성된 배송 내용
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<DeliveryCreateResponse>> createDelivery(@RequestBody DeliveryCreateRequest request) {
		DeliveryCreateResponse response = deliveryService.createDelivery(request);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 생성 성공"));
	}

	/**
	 * 배송 전체 조회 (+검색)
	 *
	 * @return : 조회한 전체 배송 내용
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<PagedModel<DeliveryGetListResponse>>> getAllDeliveries(
			Pageable pageable, @ModelAttribute DeliverySearchRequest request) {
		PagedModel<DeliveryGetListResponse> response = deliveryService.getAllDeliveries(pageable, request);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 전체 조회 성공"));
	}

	/**
	 * 배송 단건 조회
	 *
	 * @param deliveryId : 찾을 배송 ID
	 * @return : 찾은 배송 내용
	 */
	@GetMapping("/{deliveryId}")
	public ResponseEntity<CommonResponse<DeliveryGetOneResponse>> getOneDelivery(@PathVariable UUID deliveryId) {
		DeliveryGetOneResponse response = deliveryService.getOneDelivery(deliveryId);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 단건 조회 성공"));
	}

	/**
	 * 배송 수정
	 *
	 * @param deliveryId : 수정할 배송 ID
	 * @param request : 수정할 배송 내용
	 * @return : 수정한 배송 내용
	 */
	@PutMapping("/{deliveryId}")
	public ResponseEntity<CommonResponse<DeliveryUpdateResponse>> updateDelivery(
		@PathVariable UUID deliveryId, @RequestBody DeliveryUpdateRequest request) {
		DeliveryUpdateResponse response = deliveryService.updateDelivery(deliveryId, request);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 수정 성공"));
	}

	@PatchMapping("/{deliveryId}")
	public ResponseEntity<CommonResponse<DeliveryStateUpdateResponse>> updateDeliveryState(
		@PathVariable UUID deliveryId, @RequestBody DeliveryStatusUpdateRequest request) {
		DeliveryStateUpdateResponse response = deliveryService.updateDeliveryState(deliveryId, request);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 상태 수정 성공"));
	}

	/**
	 * 배송 삭제
	 * 
	 * @param deliveryId : 삭제할 배송 ID
	 * @return : 삭제한 배송 내용
	 */
	@DeleteMapping("/{deliveryId}")
	public  ResponseEntity<CommonResponse> deleteDelivery(@PathVariable UUID deliveryId) {
		deliveryService.deleteDelivery(deliveryId);
		return ResponseEntity.ok(CommonResponse.success(deliveryId, "배송 삭제 성공"));
	}


}
