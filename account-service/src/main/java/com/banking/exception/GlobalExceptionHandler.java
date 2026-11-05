package com.banking.exception;

import com.banking.exception.response.AccountExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<AccountExceptionResponse> accountNotFoundException(AccountNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(
                new AccountExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()
                ),
                status
        );
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<AccountExceptionResponse> accountStatusException(AccountStatusException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        return new ResponseEntity<>(
                new AccountExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()
                ),
                status
        );
    }

    @ExceptionHandler(AccountBalanceException.class)
    public ResponseEntity<AccountExceptionResponse> accountBalanceException(AccountBalanceException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        return new ResponseEntity<>(
                new AccountExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()
                ),
                status
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AccountExceptionResponse> genericException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        return new ResponseEntity<>(
                new AccountExceptionResponse(
                        ex.getMessage(),
                        status.value(),
                        request.getDescription(false),
                        LocalDateTime.now()
                ),
                status
        );
    }
}
