package com.example.retrofitbuilder.config.builder;

import lombok.Getter;

public class ApiRequestException extends RuntimeException {
    @Getter
    private int statusCode;

    @Getter
    private String errorCode;

    public ApiRequestException(int statusCode, String errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public ApiRequestException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public ApiRequestException(int statusCode, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}

