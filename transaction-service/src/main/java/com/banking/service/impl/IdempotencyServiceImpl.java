package com.banking.service.impl;

import com.banking.dto.response.TransactionResponse;
import com.banking.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final RedisTemplate<String,TransactionResponse> redisTemplate;

    @Override
    public Optional<TransactionResponse> get(String idempotencyKey) {

        TransactionResponse response = redisTemplate
                        .opsForValue()
                        .get("idempotencyKey:" + idempotencyKey);
        return Optional.ofNullable(response);
    }

    @Override
    public void set(String idempotencyKey, TransactionResponse response) {
        redisTemplate
                .opsForValue()
                .set(
                        "idempotencyKey:" + idempotencyKey,
                        response,
                        Duration.ofHours(24)
                );
    }
}
