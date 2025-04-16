package com.woodenfurniture.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    PERMISSION_EXISTED(1009, "Permission existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(1010, "Permission is not existed", HttpStatus.BAD_REQUEST),
    ROLE_EXISTED(1011, "Role existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(1012, "Role is not existed", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1013, "Invalid token", HttpStatus.BAD_REQUEST),
    TOKEN_DISABLED(1014, "This token has been disabled", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1015, "Invalid request", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1016, "Email existed", HttpStatus.BAD_REQUEST),
    // Add more error codes above
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
