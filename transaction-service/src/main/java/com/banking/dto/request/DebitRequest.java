package com.banking.dto.request;

import com.banking.constant.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitRequest {

    private String idempotencyKey;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Amount must be >= 1")
    @DecimalMin(value = "1.0", message = "Amount must be >= 1")
    private BigDecimal amount;

    private String description;

}
