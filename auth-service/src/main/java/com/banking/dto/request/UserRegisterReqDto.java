package com.banking.dto.request;

import com.banking.constant.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterReqDto {

    private String email;
    private String password;
    private String fullName;
    private Set<UserRole> roles;
}
