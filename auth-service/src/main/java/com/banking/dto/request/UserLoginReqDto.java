package com.banking.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginReqDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    private String refreshToken;
}
