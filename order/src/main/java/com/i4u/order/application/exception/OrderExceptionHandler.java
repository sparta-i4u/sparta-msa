package com.i4u.order.application.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.i4u.common.utils.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class OrderExceptionHandler {

	@ExceptionHandler(exception = {OrderException.class})
	public ResponseEntity<CommonResponse<OrderException>> errorResponse(OrderException exception) {
		log.error("[ErrorCode] = {} , [ErrorMessage] = {}", exception.getErrorCode(), exception.getMessage());
		// CommonResponse.fail 메소드 수정 가능한지 확인하기
		return ResponseEntity.status(exception.getStatus()).body(CommonResponse.fail());
	}

}
