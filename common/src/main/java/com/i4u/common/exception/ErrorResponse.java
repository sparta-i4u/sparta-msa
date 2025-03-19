package com.i4u.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ✅ 모든 예외 응답을 통일된 형식으로 반환하는 클래스
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final String errorCode; // 예외 코드
    private final String message;   // 예외 메시지
    private final int status;       // HTTP 상태 코드

    //`CustomException`을 기반으로 `ErrorResponse` 생성
    public static ErrorResponse from(CustomException ex) {
        return new ErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getStatus().value());
    }

    //  기본적인 예외 응답 생성

    public static ErrorResponse of(String errorCode, String message, HttpStatus status) {
        return new ErrorResponse(errorCode, message, status.value());
    }
}
