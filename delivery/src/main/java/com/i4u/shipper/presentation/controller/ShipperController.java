package com.i4u.shipper.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.application.dto.ShipperCreateRequest;
import com.i4u.shipper.application.dto.ShipperResponse;
import com.i4u.shipper.application.dto.ShipperUpdateRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/shippers")
@RequiredArgsConstructor
public class ShipperController {

	/**
	 * 배송 담당자 생성
	 * @param shipperCreateRequest : 배송 담당자 생성 정보
	 * @return : 생성한 배송 담당자 내용
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<ShipperResponse>> createShipper(@RequestBody ShipperCreateRequest shipperCreateRequest) {
		return ResponseEntity.ok(CommonResponse.success(ShipperResponse.createDto(shipperCreateRequest), "배송 담당자 생성 성공"));
	}

	/**
	 * 배송 담당자 전체 조회 (+검색)
	 * @return : 조회한 전체 배송 담당자 내용
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<List<ShipperResponse>>> getAllShippers() {
		// Pagination 적용 필요
		return ResponseEntity.ok(CommonResponse.success(List.of(ShipperResponse.getDto()), "배송 담당자 전체 조회 성공"));
	}

	/**
	 * 배송 담당자 단건 조회
	 * @param shipperId : 조회할 배송 담당자 ID
	 * @return : 조회한 배송 담당자 내용
	 */
	@GetMapping("/{shipperId}")
	public ResponseEntity<CommonResponse<ShipperResponse>> getOneShipper(@PathVariable UUID shipperId) {
		return ResponseEntity.ok(CommonResponse.success(ShipperResponse.getDto(), "배송 담당자 단건 조회 성공"));
	}

	/**
	 * 배송 담당자 수정
	 * @param shipperId : 수정할 배송 담당자 ID
	 * @return : 수정한 배송 담당자 내용
	 */
	@PutMapping("/{shipperId}")
	public ResponseEntity<CommonResponse<ShipperResponse>> putShipper(@PathVariable UUID shipperId) {
		return ResponseEntity.ok(CommonResponse.success(ShipperResponse.updateDto(ShipperUpdateRequest.builder().build()), "배송 담당자 수정 성공"));
	}

	/**
	 * 배송 담당자 삭제
	 * @param shipperId : 삭제할 배송 담당자 ID
	 * @return : 삭제한 배송 담당자 내용
	 */
	@DeleteMapping("/{shipperId}")
	public ResponseEntity<CommonResponse<ShipperResponse>> deleteShipper(@PathVariable UUID shipperId) {
		return ResponseEntity.ok(CommonResponse.success(null, "배송 담당자 삭제 성공"));
	}

}