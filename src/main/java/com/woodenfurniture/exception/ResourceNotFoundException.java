package com.woodenfurniture.exception;

import lombok.Data;

@Data
public class ResourceNotFoundException extends RuntimeException {
    private String errorMessage;

    public ResourceNotFoundException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
