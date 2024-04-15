package com.woodenfurniture.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error"),
    INVALID_KEY( 1001, "In valid message key"),
    USER_EXISTED(1002, "User existed"),
    USER_NOT_FOUND( 404, "User not found"),
    USERNAME_INVALID( 1003, "Username must be at least 3 characters"),
    INVALID_PASSWORD( 1004, "Password must be at least 8 characters"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;
}
