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
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    private String idempotencyKey;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Debit account number is required")
    private String fromAccount;

    @NotNull(message = "Credit account number is required")
    private String toAccount;

    @NotNull
    @DecimalMin(value = "1.0", message = "Amount must be >= 1")
    private BigDecimal amount;

    private String description;
}
