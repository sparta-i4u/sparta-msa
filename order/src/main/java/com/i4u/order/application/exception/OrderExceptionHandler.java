package com.i4u.order.application.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.i4u.common.utils.CommonResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class OrderExceptionHandler {

	@ExceptionHandler(exception = {OrderException.class})
	public ResponseEntity<CommonResponse<OrderException>> errorResponse(OrderException exception) {
		log.error("[ErrorCode] = {} , [ErrorMessage] = {}", exception.getErrorCode(), exception.getMessage());
		// CommonResponse.fail 메소드 수정 가능한지 확인하기
		return ResponseEntity.status(exception.getStatus()).body(CommonResponse.fail(
			"400", exception.getMessage(), HttpStatus.BAD_REQUEST.value()
		));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<CommonResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		String error = String.format("잘못된 값 입력: '%s'. '%s' 타입이어야 합니다.",
			exception.getValue(), exception.getRequiredType().getSimpleName());

		log.error("[ErrorCode] = {} , [ErrorMessage] = {}", exception.getErrorCode(), error);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail("400", exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponse> handleValidationExceptions(MethodArgumentNotValidException exception) {
		List<String> errors = exception.getBindingResult().getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.toList();

		log.warn("[Validation 오류]: {}", errors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail("400", exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<CommonResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		log.warn("[Invalid request body] = {}", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(CommonResponse.fail("400", exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
	}

}
