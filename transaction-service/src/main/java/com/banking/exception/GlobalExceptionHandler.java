package com.banking.exception;

import com.banking.exception.response.TransactionExceptionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(TransactionNotFound.class)
    public ResponseEntity<TransactionExceptionResponse> handleTransactionNotFound(TransactionNotFound ex, WebRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(
               new TransactionExceptionResponse(
                       ex.getMessage(),
                       httpStatus.value(),
                       request.getDescription(false),
                       LocalDateTime.now()
               ),
                httpStatus
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<TransactionExceptionResponse> handleFeignException(FeignException ex, WebRequest request) throws JsonProcessingException {

        HttpStatus httpStatus = HttpStatus.valueOf(ex.status());

        TransactionExceptionResponse errorResponse =
                objectMapper.readValue(
                        ex.contentUTF8(),
                        TransactionExceptionResponse.class
                );
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(request.getDescription(false));
        return new ResponseEntity<>(
                errorResponse,
                httpStatus
        );
    }
}
