package com.banking.dto;


import com.banking.constant.AccountType;
import com.banking.constant.KycStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCrtReqDto {

    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "100.0",message = "Minimum balance cannot be less than Rs.100")
    private BigDecimal balance;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{10}", message = "Invalid mobile number")
    private String mobileNumber;

    @NotBlank(message = "Pin code is required")
    @Pattern(regexp = "\\d{6}", message = "Invalid pin/zip code")
    private String pinCode;

    @NotBlank(message = "Address is required")
    private String address;

    private KycStatus kycStatus=KycStatus.COMPLETED;

    @CurrentTimestamp
    private LocalDateTime KycUpdatedAt;
}

