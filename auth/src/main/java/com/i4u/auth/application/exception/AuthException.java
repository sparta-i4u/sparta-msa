package com.i4u.auth.application.exception;

import com.i4u.common.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends CustomException {

    public AuthException(AuthErrorType errorType) {
        super(errorType.getCode(), errorType.getMessage(), errorType.getStatus());
    }

    public enum AuthErrorType {
        AUTHENTICATION_FAILED("AUTH_001", HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),
        TOKEN_EXPIRED("AUTH_002", HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
        INVALID_CREDENTIALS("AUTH_003", HttpStatus.BAD_REQUEST, "잘못된 인증 정보입니다."),
        PERMISSION_DENIED("AUTH_004", HttpStatus.FORBIDDEN, "권한이 없습니다."),
        USER_NOT_FOUND("AUTH_005", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
        INTERNAL_SERVER_ERROR("AUTH_006", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;

        AuthErrorType(String code, HttpStatus status, String message) {
            this.code = code;
            this.status = status;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}