package com.i4u.shipper.presentation.controller;

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

	private final ShipperService shipperService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(UUID.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				setValue(UUID.fromString(text));
			}
		});
	}

	/**
	 * 배송 담당자 생성
	 *
	 * @param shipperCreateRequest : 배송 담당자 생성 정보
	 * @return : 생성한 배송 담당자 내용
	 */
	@PostMapping // MASTER, HUB_MANAGER(담당 허브)
	public ResponseEntity<CommonResponse<ShipperCreateResponse>> createShipper(
		@RequestBody ShipperCreateRequest shipperCreateRequest,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("배송 담당자 생성 요청 들어옴");
		log.info("userId: " + userId);
		ShipperCreateResponse response = shipperService.createShipper(shipperCreateRequest, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 생성 성공"));
	}

	/**
	 * 배송 담당자 전체 조회 (+검색)
	 *
	 * @param pageable : 페이지네이션 (페이지 수 page, 한 페이지의 데이터 개수 size)
	 * @return : 조회한 전체 배송 담당자 내용
	 */
	@GetMapping // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 정보)
	public ResponseEntity<CommonResponse<PagedModel<ShipperListResponse>>> getAllShippers(
		Pageable pageable, @ModelAttribute ShipperSearchRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("배송 담당자 전제 조회 요청 들어옴");
		PagedModel<ShipperListResponse> shipperList = shipperService.getAllShippers(pageable, request, userId, role);
		return ResponseEntity.ok(CommonResponse.success(shipperList, "배송 담당자 전체 조회 성공"));
	}

	/**
	 * 배송 담당자 단건 조회
	 *
	 * @param shipperId : 조회할 배송 담당자 ID
	 * @return : 조회한 배송 담당자 내용
	 */
	@GetMapping("/{shipperId}") // MASTER, HUB_MANAGER(담당 허브), DELIVERY_MANAGER(본인 정보)
	public ResponseEntity<CommonResponse<ShipperGetOneResponse>> getOneShipper(
		@PathVariable UUID shipperId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("배송 담당자 단건 조회 요청 들어옴 : " + shipperId);
		ShipperGetOneResponse response = shipperService.getOneShipper(shipperId, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 단건 조회 성공"));
	}

	/**
	 * 배송 담당자 수정
	 *
	 * @param shipperId : 수정할 배송 담당자 ID
	 * @return : 수정한 배송 담당자 내용
	 */
	@PutMapping("/{shipperId}") // MASTER, HUB_MANAGER(담당 허브)
	public ResponseEntity<CommonResponse<ShipperUpdateResponse>> putShipper(
		@PathVariable UUID shipperId, @RequestBody ShipperUpdateRequest shipperUpdateRequest,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("배송 담당자 수정 요청 들어옴 : " + shipperId);
		log.info(shipperUpdateRequest.getShipperType().toString());
		ShipperUpdateResponse response = shipperService.updateShipper(shipperId, shipperUpdateRequest, userId, role);
		return ResponseEntity.ok(CommonResponse.success(response, "배송 담당자 수정 성공"));
	}

	/**
	 * 배송 담당자 삭제
	 *
	 * @param shipperId : 삭제할 배송 담당자 ID
	 * @return : 삭제한 배송 담당자 내용
	 */
	@DeleteMapping("/{shipperId}") // MASTER, HUB_MANAGER(담당 허브)
	public ResponseEntity<CommonResponse> deleteShipper(
		@PathVariable UUID shipperId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) {
		log.info("배송 담당자 삭제 요청 들어옴 : " + shipperId);
		shipperService.deleteShipper(shipperId, userId, role);
		return ResponseEntity.ok(CommonResponse.success(shipperId, "배송 담당자 삭제 성공"));
	}

}