package com.i4u.order.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "주문 API", description = "주문 API 정리")
public interface OrderApi {

	// 주문 생성
	@Operation(summary = "주문 생성", description = "주문을 생성하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "주문 생성 성공",
				content = @Content(schema = @Schema(implementation = OrderCreateResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<?>> createOrder(
		@Valid @RequestBody OrderCreateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role);


	// 주문 전체 조회
	@Operation(summary = "주문 전체 조회", description = "전체 주문을 조회하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "주문 전체 조회 성공",
				content = @Content(schema = @Schema(implementation = OrderGetListResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<?>> getAllOrders(
		Pageable pageable, @ModelAttribute OrderSearchRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role);


	// 주문 단건 조회
	@Operation(summary = "주문 단건 조회", description = "단건 주문을 조회하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "주문 단건 조회 성공",
				content = @Content(schema = @Schema(implementation = OrderGetOneResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<?>> getOneOrder(
		@PathVariable UUID orderId,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role);


	// 주문 수정
	@Operation(summary = "주문 수정", description = "주문을 수정하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "주문 수정 성공",
				content = @Content(schema = @Schema(implementation = OrderUpdateResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<?>> putOrder(
		@PathVariable UUID orderId, @Valid @RequestBody OrderUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role) ;


	// 주문 상태 수정
	@Operation(summary = "주문 상태 수정", description = "주문 상태를 수정하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "주문 상태 수정 성공",
				content = @Content(schema = @Schema(implementation = OrderStatusUpdateResponse.class)))
		}
	)
	ResponseEntity<CommonResponse<?>> patchOrder(
		@PathVariable UUID orderId, @RequestBody OrderStatusUpdateRequest request,
		@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role);


	// 주문 삭제
	@Operation(summary = "주문 삭제", description = "주문을 삭제하는 API")
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "주문 삭제",
				content = @Content(schema = @Schema(implementation = OrderStatusUpdateResponse.class)))
		}
	)
	ResponseEntity<CommonResponse> deleteOrder(
		@PathVariable UUID orderId,	@RequestHeader(name = "X-User-Id") UUID userId,
		@RequestHeader(name = "X-User-Role") String role);

}
