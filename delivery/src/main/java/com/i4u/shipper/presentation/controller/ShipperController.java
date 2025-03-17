package com.i4u.shipper.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.application.dtos.request.ShipperCreateRequest;
import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.request.ShipperUpdateRequest;
import com.i4u.shipper.application.dtos.response.ShipperCreateResponse;
import com.i4u.shipper.application.dtos.response.ShipperGetOneResponse;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;
import com.i4u.shipper.application.dtos.response.ShipperUpdateResponse;
import com.i4u.shipper.application.service.ShipperService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/shippers")
@RequiredArgsConstructor
public class ShipperController {

	// TODO : 전체적으로 권한 확인 어떻게 할지 생각하기

	private final ShipperService shipperService;

	/**
	 * 배송 담당자 생성
	 *
	 * @param shipperCreateRequest : 배송 담당자 생성 정보
	 * @return : 생성한 배송 담당자 내용
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<ShipperCreateResponse>> createShipper(@RequestBody ShipperCreateRequest shipperCreateRequest) {
		log.info("배송 담당자 생성 요청 들어옴");
		ShipperCreateResponse response = shipperService.createShipper(shipperCreateRequest);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 생성 성공"));
	}

	/**
	 * 배송 담당자 전체 조회 (+검색)
	 *
	 * @param pageable : 페이지네이션 (페이지 수 page, 한 페이지의 데이터 개수 size)
	 * @return : 조회한 전체 배송 담당자 내용
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<List<ShipperListResponse>>> getAllShippers(
		Pageable pageable, @ModelAttribute ShipperSearchRequest request) {
		log.info("배송 담당자 전제 조회 요청 들어옴");
		List<ShipperListResponse> shipperList = shipperService.getAllShippers(pageable, request);
		return ResponseEntity.ok(CommonResponse.success(shipperList, "배송 담당자 전체 조회 성공"));
	}

	/**
	 * 배송 담당자 단건 조회
	 *
	 * @param shipperId : 조회할 배송 담당자 ID
	 * @return : 조회한 배송 담당자 내용
	 */
	@GetMapping("/{shipperId}")
	public ResponseEntity<CommonResponse<ShipperGetOneResponse>> getOneShipper(@PathVariable UUID shipperId) {
		log.info("배송 담당자 단건 조회 요청 들어옴 : " + shipperId);
		ShipperGetOneResponse response = shipperService.getOneShipper(shipperId);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 단건 조회 성공"));
	}

	/**
	 * 배송 담당자 수정
	 *
	 * @param shipperId : 수정할 배송 담당자 ID
	 * @return : 수정한 배송 담당자 내용
	 */
	@PutMapping("/{shipperId}")
	public ResponseEntity<CommonResponse<ShipperUpdateResponse>> putShipper(@PathVariable UUID shipperId, ShipperUpdateRequest shipperUpdateRequest) {
		log.info("배송 담당자 수정 요청 들어옴 : " + shipperId);
		ShipperUpdateResponse response = shipperService.updateShipper(shipperId, shipperUpdateRequest);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 수정 성공"));
	}

	/**
	 * 배송 담당자 삭제
	 *
	 * @param shipperId : 삭제할 배송 담당자 ID
	 * @return : 삭제한 배송 담당자 내용
	 */
	@DeleteMapping("/{shipperId}")
	public ResponseEntity<CommonResponse> deleteShipper(@PathVariable UUID shipperId) {
		log.info("배송 담당자 삭제 요청 들어옴 : " + shipperId);
		shipperService.deleteShipper(shipperId);
		return ResponseEntity.ok(CommonResponse.success(shipperId, "배송 담당자 삭제 성공"));
	}

}