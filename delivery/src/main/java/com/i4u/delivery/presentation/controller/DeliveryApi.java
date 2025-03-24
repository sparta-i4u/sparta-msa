package com.i4u.delivery.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "배송 API", description = "배송 API 정리")
public interface DeliveryApi {

	// 배송 생성
	@Operation(summary = "배송 생성", description = "배송을 생성하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 생성 성공",
				content = @Content(schema = @Schema(implementation = DeliveryCreateResponse.class)))
		}
	)
	DeliveryCreateResponse createDelivery(@RequestBody DeliveryCreateRequest request);

	// 배송 전체 조회 + 검색
	@Operation(summary = "배송 전체 조회", description = "전체 배송을 조회하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 목록 조회 성공",
				content = @Content(schema = @Schema(implementation = DeliveryGetListResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<PagedModel<DeliveryGetListResponse>>> getAllDeliveries(
		Pageable pageable, @ModelAttribute DeliverySearchRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,	@RequestHeader(name = "X-User-Role") String role);

	// 배송 단건 조회
	@Operation(summary = "배송 단건 조회", description = "단건 배송을 조회하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 단건 조회 성공",
				content = @Content(schema = @Schema(implementation = DeliveryGetOneResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<DeliveryGetOneResponse>> getOneDelivery(
		@PathVariable UUID deliveryId, @RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role);

	// 배송 수정
	@Operation(summary = "배송 수정", description = "배송을 수정하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 수정 성공",
				content = @Content(schema = @Schema(implementation = DeliveryUpdateResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<DeliveryUpdateResponse>> updateDelivery(
		@PathVariable UUID deliveryId, @RequestBody DeliveryUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,	@RequestHeader(name = "X-User-Role") String role);
	
	// 배송 상태 수정
	@Operation(summary = "배송 상태 수정", description = "배송 상태를 수정하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 상태 수정 성공",
				content = @Content(schema = @Schema(implementation = DeliveryStateUpdateResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<DeliveryStateUpdateResponse>> updateDeliveryState(
		@PathVariable UUID deliveryId, @RequestBody DeliveryStatusUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,	@RequestHeader(name = "X-User-Role") String role) ;

	// 배송 삭제
	@Operation(summary = "배송 삭제", description = "배송을 삭제하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 삭제 성공",
				content = @Content(schema = @Schema(implementation = CommonResponse.class)))
		}
	)
	ResponseEntity<CommonResponse> deleteDelivery(
		@PathVariable UUID deliveryId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) ;

}
