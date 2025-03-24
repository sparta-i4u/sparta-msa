package com.i4u.shipper.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.i4u.common.utils.CommonResponse;
import com.i4u.shipper.application.dtos.request.ShipperCreateRequest;
import com.i4u.shipper.application.dtos.request.ShipperSearchRequest;
import com.i4u.shipper.application.dtos.request.ShipperUpdateRequest;
import com.i4u.shipper.application.dtos.response.ShipperCreateResponse;
import com.i4u.shipper.application.dtos.response.ShipperGetOneResponse;
import com.i4u.shipper.application.dtos.response.ShipperListResponse;
import com.i4u.shipper.application.dtos.response.ShipperUpdateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "배송 담당자 API", description = "배송 담당자 API 정리")
public interface ShipperApi {

	// 배송 담당자 생성
	@Operation(summary = "배송 담당자 생성", description = "배송 담당자를 생성하는 API")
			@ApiResponses(
				value = {
					@ApiResponse(responseCode = "200", description = "배송 담당자 생성 성공",
						content = @Content(schema = @Schema(implementation = ShipperCreateResponse.class)))
				}
			)
	ResponseEntity<CommonResponse<ShipperCreateResponse>> createShipper(
		@RequestBody ShipperCreateRequest shipperCreateRequest,
		@RequestHeader(name = "X-User-Id") UUID userId,	@RequestHeader(name = "X-User-Role") String role);


	// 배송 담당자 전체 조회
	@Operation(summary = "배송 담당자 전체 조회", description = "전체 배송 담당자를 조회하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 담당자 전체 조회 성공",
				content = @Content(schema = @Schema(implementation = ShipperListResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<PagedModel<ShipperListResponse>>> getAllShippers(
		Pageable pageable, @ModelAttribute ShipperSearchRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,	@RequestHeader(name = "X-User-Role") String role);

	// 배송 담당자 단건 조회
	@Operation(summary = "배송 담당자 단건 조회", description = "단건 배송 담당자를 조회하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 담당자 단건 조회 성공",
				content = @Content(schema = @Schema(implementation = ShipperGetOneResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<ShipperGetOneResponse>> getOneShipper(
		@PathVariable UUID shipperId, @RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) ;
	
	// 배송 담당자 수정
	@Operation(summary = "배송 담당자 수정", description = "배송 담당자를 수정하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 담당자 수정 성공",
				content = @Content(schema = @Schema(implementation = ShipperUpdateResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<ShipperUpdateResponse>> putShipper(
		@PathVariable UUID shipperId, @RequestBody ShipperUpdateRequest shipperUpdateRequest,
		@RequestHeader(name = "X-User-Id") UUID userId,	@RequestHeader(name = "X-User-Role") String role) ;


	// 배송 담당자 삭제
	@Operation(summary = "배송 담당자 삭제", description = "배송 담당자를 삭제하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "배송 담당자 삭제 성공",
				content = @Content(schema = @Schema(implementation = CommonResponse.class)))
		}
	)
	ResponseEntity<CommonResponse> deleteShipper(
		@PathVariable UUID shipperId, @RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role);

}
