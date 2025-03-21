package com.i4u.delivery.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.i4u.common.utils.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class DeliveryExceptionHandler {

	@ExceptionHandler(exception = {DeliveryException.class})
	public ResponseEntity<CommonResponse<DeliveryException>> errorResponse(DeliveryException exception) {
		log.error("[ErrorCode] = {} , [ErrorMessage] = {}", exception.getErrorCode(), exception.getMessage());
		// CommonResponse.fail 메소드 수정 가능한지 확인하기
		return ResponseEntity.status(exception.getStatus()).body(CommonResponse.fail(
			"500", exception.getMessage(), HttpStatus.BAD_REQUEST.value()
		));
	}

}