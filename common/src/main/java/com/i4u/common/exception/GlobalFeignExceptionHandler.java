package com.i4u.common.exception;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;

// FeignClient 및 공통 예외 처리 핸들러
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalFeignExceptionHandler implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String message = getErrorMessage(response);

        return new CustomException(status.name(), message, status);
    }

    // `CustomException` 처리 핸들러
    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustomException(CustomException ex) {
        return ErrorResponse.from(ex); // ✅ `ErrorResponse` 사용
    }

    // `FeignException` 처리 핸들러
    @ExceptionHandler(FeignException.class)
    public ErrorResponse handleFeignException(FeignException ex) {
        return ErrorResponse.of("FEIGN_ERROR", "FeignClient 오류 발생: " + ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    // 기타 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGlobalException(Exception ex) {
        return ErrorResponse.of("GLOBAL_ERROR", "서버 내부 오류 발생: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getErrorMessage(Response response) {
        try {
            if (response.body() != null) {
                return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception ignored) {}
        return "FeignClient 호출 중 오류 발생";
    }
}
