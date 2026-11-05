package com.banking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private UUID accountId;
    private String accountNumber;
    private UUID userId;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String status;
    private String mobileNumber;
    private String pinCode;
    private String address;
    private String kycStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime kycUpdatedAt;
}
