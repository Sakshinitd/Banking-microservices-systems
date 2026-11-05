package com.banking.dto;

import com.banking.constant.KycStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountKycReqDto {

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{10}", message = "Invalid mobile number")
    private String mobileNumber;

    @NotBlank(message = "Pin code is required")
    @Pattern(regexp = "\\d{6}", message = "Invalid pin/zip code")
    private String pinCode;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull
    private KycStatus kycStatus;

    @CurrentTimestamp
    private LocalDateTime KycUpdatedAt;
}
