package com.banking.mapper;

import com.banking.dto.AccountCrtReqDto;
import com.banking.dto.AccountKycReqDto;
import com.banking.entity.Account;

import java.time.LocalDateTime;

public class AccountMapper {

    public static Account mapAccountCrtReqDtoToAccount(AccountCrtReqDto accountCrtReqDto) {
        Account account = new Account();
        account.setUserId(accountCrtReqDto.getUserId());
        account.setAccountType(accountCrtReqDto.getAccountType());
        account.setBalance(accountCrtReqDto.getBalance());
        account.setCurrency(accountCrtReqDto.getCurrency());
        account.setMobileNumber(accountCrtReqDto.getMobileNumber());
        account.setPinCode(accountCrtReqDto.getPinCode());
        account.setAddress(accountCrtReqDto.getAddress());
        account.setKycStatus(accountCrtReqDto.getKycStatus());
        account.setKycUpdatedAt(LocalDateTime.now());
        return account;
    }

    public static Account mapAccountKycReqDtoToAccount(AccountKycReqDto accountKycReqDto) {
        Account account = new Account();
        account.setMobileNumber(accountKycReqDto.getMobileNumber());
        account.setPinCode(accountKycReqDto.getPinCode());
        account.setAddress(accountKycReqDto.getAddress());
        account.setKycStatus(accountKycReqDto.getKycStatus());
        account.setKycUpdatedAt(LocalDateTime.now());
        return account;
    }
}
