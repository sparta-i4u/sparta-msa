package com.i4u.auth.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private final HttpStatus status;

    public AuthException(AuthErrorType errorType) {
        super(errorType.getMessage());
        this.status = errorType.getStatus();
    }

    public enum AuthErrorType {
        AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
        TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
        INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "잘못된 인증 정보입니다."),
        PERMISSION_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
        USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

        private final HttpStatus status;
        private final String message;

        AuthErrorType(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
