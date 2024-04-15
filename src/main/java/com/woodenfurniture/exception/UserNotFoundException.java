package com.woodenfurniture.exception;

import lombok.Data;

@Data
public class UserNotFoundException extends RuntimeException{
    private ErrorCode errorCode;

    public UserNotFoundException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
