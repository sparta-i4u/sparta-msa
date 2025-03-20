// 📌 ErrorResponse.java 수정 (Common 모듈)
package com.i4u.common.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"status", "code", "message"}) // ✅ JSON 필드 순서 유지
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;

    public static ErrorResponse from(CustomException ex) {
        return new ErrorResponse(ex.getStatus().value(), ex.getErrorCode(), ex.getMessage());
    }

    public static ErrorResponse of(String errorCode, String message, HttpStatus status) {
        return new ErrorResponse(status.value(), errorCode, message);
    }
}
