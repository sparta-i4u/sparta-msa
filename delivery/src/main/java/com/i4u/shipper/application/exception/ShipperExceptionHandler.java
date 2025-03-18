package com.i4u.shipper.application.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.i4u.common.utils.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ShipperExceptionHandler {

	@ExceptionHandler(exception = {ShipperException.class})
	public ResponseEntity<CommonResponse<ShipperException>> errorResponse(ShipperException exception) {
		log.error("[ErrorCode] = {} , [ErrorMessage] = {}", exception.getErrorCode(), exception.getMessage());
		// CommonResponse.fail 메소드 수정 가능한지 확인하기
		return ResponseEntity.status(exception.getStatus()).body(CommonResponse.fail());
	}

}