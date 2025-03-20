package com.i4u.common.exception;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalFeignExceptionHandler implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        int statusCode = response.status();
        String errorMessage = getErrorMessage(response);

        log.error("요청 실패 - Method: {}, Status: {}, Message: {}", methodKey, statusCode, errorMessage);

        return new CustomException("FEIGN_" + statusCode, errorMessage, HttpStatus.valueOf(statusCode));
    }

    private String getErrorMessage(Response response) {
        if (response == null || response.body() == null) {
            log.error("응답이 `null`이거나 본문이 비어 있음");
            return "FeignClient 호출 중 오류 발생 (응답이 비어 있음)";
        }
        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("응답 본문을 읽을 수 없음: {}", e.getMessage());
            return "FeignClient 오류 발생 (응답 본문을 읽을 수 없음)";
        }
    }
}
