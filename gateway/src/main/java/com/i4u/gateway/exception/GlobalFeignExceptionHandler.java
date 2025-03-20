package com.i4u.gateway.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalFeignExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException e) {
        log.error("⛔ Feign Client 오류 발생: Status={} Message={}", e.status(), e.getMessage());
        HttpStatus status = HttpStatus.resolve(e.status());
        return ResponseEntity.status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Feign Client 오류: " + e.getMessage());
    }
}
//Feign 예외(FeignException) 발생 시 자동으로 로그 출력
//HTTP 상태 코드에 맞게 예외 응답 반환
//Feign 호출 오류가 발생하면 500 대신 적절한 상태 코드 반환