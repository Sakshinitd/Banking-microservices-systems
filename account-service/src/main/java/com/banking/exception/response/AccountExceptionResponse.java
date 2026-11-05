package com.banking.exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountExceptionResponse {

    private String message;
    private int errorCode;
    private String path;
    private LocalDateTime timestamp;

}
