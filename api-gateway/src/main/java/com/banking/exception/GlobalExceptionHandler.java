package com.banking.exception;

import com.banking.exception.response.InvalidJwtExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<InvalidJwtExceptionResponse> invalidJwtException(InvalidJwtException ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(
                new InvalidJwtExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()
                ),
                status
        );
    }
}
