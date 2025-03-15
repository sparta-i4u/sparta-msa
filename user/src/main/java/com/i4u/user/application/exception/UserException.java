package com.i4u.user.application.exception;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public UserException(UserErrorType errorType) {
        super(errorType.getMessage());
        this.status = errorType.getStatus();
        this.message = errorType.getMessage();
    }

    public enum UserErrorType {
        USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
        DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 사용 중인 사용자명입니다."),
        DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
        INVALID_ROLE(HttpStatus.BAD_REQUEST, "잘못된 사용자 역할입니다."),
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

        private final HttpStatus status;
        private final String message;

        UserErrorType(HttpStatus status, String message) {
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

