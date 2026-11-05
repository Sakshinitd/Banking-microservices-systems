package com.banking.service;

import com.banking.dto.request.DebitRequest;
import com.banking.dto.response.TransactionResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface IdempotencyService {

    Optional<TransactionResponse> get(String idempotencyKey);
    void set(String idempotencyKey, TransactionResponse response);
}
