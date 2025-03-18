package com.i4u.shipper.application.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<CommonResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		String error = String.format("잘못된 값 입력: '%s'. '%s' 타입이어야 합니다.",
			exception.getValue(), exception.getRequiredType().getSimpleName());

		log.error("[ErrorCode] = {} , [ErrorMessage] = {}", exception.getErrorCode(), error);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail(/*"Invalid request format", "올바른 요청이 아닙니다."*/));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<CommonResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		log.warn("[Invalid request body] = {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail(/*"Invalid request format", "올바른 요청 데이터가 아닙니다."*/));
	}

}