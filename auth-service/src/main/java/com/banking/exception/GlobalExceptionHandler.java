package com.banking.exception;

import com.banking.exception.response.UserExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserExceptionResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(
                new UserExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()),
                status
        );
    }

    @ExceptionHandler(InvalidUserCredentials.class)
    public ResponseEntity<UserExceptionResponse> handleInvalidCredentials(InvalidUserCredentials ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(
                new UserExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()),
                status
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserExceptionResponse> genericException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(
                new UserExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()),
                status
        );
    }
}
