package com.i4u.order.presentation.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.order.application.dto.OrderCreateRequestDto;
import com.i4u.order.application.dto.OrderResponseDto;
import com.i4u.order.application.dto.OrderStatusUpdateRequestDto;
import com.i4u.order.application.dto.OrderUpdateRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

	/**
	 * 주문 생성
	 * @param orderCreateRequestDto : 생성할 주문의 정보
	 * @return : 생성된 주문 내용
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<OrderResponseDto>> createOrder(@RequestBody OrderCreateRequestDto orderCreateRequestDto){
		// gateway로부터 사용자의 정보를 어떻게 받아올 것인지 생각해보기 (역할은 필수로 필요)
		// order에서는 필요 없지만, 여기서 받아서 바로 배송 쪽으로 정보가 넘어가야 하므로 수령인에 대한 정보가 필요 (id, slackid)
		return ResponseEntity.ok(CommonResponse.success(OrderResponseDto.createSampleDto(orderCreateRequestDto), "생성 성공"));
	}

	/**
	 * 주문 전체 조회 (+검색)
	 * @return : 조회된 전체 주문 내용
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<List<OrderResponseDto>>> getAllOrders() {
		// 검색 기능 적용 예정으로 Pagination 내용으로 변경하기
		return ResponseEntity.ok(CommonResponse.success(List.of(OrderResponseDto.getSampleDto()), "전체 조회 성공"));
	}

	/**
	 * 주문 단건 조회
	 * @param orderId : 조회할 주문의 ID
	 * @return : 조회된 주문 내용
	 */
	@GetMapping("/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponseDto>> getOneOrder(@PathVariable UUID orderId) {
		return ResponseEntity.ok(CommonResponse.success(OrderResponseDto.getSampleDto(), "단건 조회 성공"));
	}

	/**
	 * 주문 수정
	 * @param orderId : 수정할 주문의 ID
	 * @param orderUpdateRequestDto : 수정할 주문 정보
	 * @return : 수정된 주문 내용
	 */
	@PutMapping("/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponseDto>> putOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateRequestDto orderUpdateRequestDto) {
		// 주문 상태 확인 필수
		return ResponseEntity.ok(CommonResponse.success(OrderResponseDto.updateSampleDto(orderUpdateRequestDto), "수정 성공"));
	}

	/**
	 * 주문 상태 수정
	 * @param orderId : 상태를 수정할 주문의 ID
	 * @param orderStatusUpdateRequestDto : 수정할 주문 상태 정보
	 * @return : 상태가 수정된 주문 내용
	 */
	@PatchMapping("/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponseDto>> patchOrder(@PathVariable UUID orderId, @RequestBody OrderStatusUpdateRequestDto orderStatusUpdateRequestDto) {
		return ResponseEntity.ok(CommonResponse.success(OrderResponseDto.updateStatusSampleDto(orderStatusUpdateRequestDto), "상태 수정 성공"));
	}

	/**
	 * 주문 삭제
	 * @param orderId : 삭제할 주문의 ID
	 * @return : 삭제 완료된 주문 내용
	 */
	@DeleteMapping("/{orderId}")
	public ResponseEntity<CommonResponse> deleteOrder(@PathVariable UUID orderId) {
		return ResponseEntity.ok(CommonResponse.success(null, "삭제 성공"));
	}

	/*
	* 주문 상태
	* 결제 완료, 배송 확인 (?), 출고 전, 배송 전, 배송 중, 배송 완료, 주문 취소
	*
	* Order와 OrderItem을 하나의 Aggregate로 가져간다고 생각하기 (일단 스코프가 크지 않으니까)
	*/

}
