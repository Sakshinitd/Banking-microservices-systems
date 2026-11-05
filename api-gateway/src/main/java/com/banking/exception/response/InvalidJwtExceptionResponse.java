package com.banking.exception.response;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class InvalidJwtExceptionResponse {


    private String message;
    private int errorCode;
    private String path;
    private LocalDateTime timestamp;

    public InvalidJwtExceptionResponse(String message, int errorCode, String path, LocalDateTime timestamp) {
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.timestamp = timestamp;
    }

    public InvalidJwtExceptionResponse(){

    }



}
