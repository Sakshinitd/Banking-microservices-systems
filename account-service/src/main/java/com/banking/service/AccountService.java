package com.banking.service;

import com.banking.dto.AccountCrtReqDto;
import com.banking.dto.AccountKycReqDto;
import com.banking.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account createAccount(AccountCrtReqDto accountCrtReqDto, String email);
    Account findByAccountNumber(String accountNumber);
    BigDecimal getBalance(String accountNumber);
    Account updateAccountKycDetails(UUID accountId, AccountKycReqDto accountKycReqDto, String email);
    Account freezeAccount(UUID accountId, String email);
    List<Account> getAllAccountsOfCustomer(UUID userId);
    String debit(String accountNumber, BigDecimal amount);
    String credit(String accountNumber, BigDecimal amount);
    String transfer(String fromAccount,String toAccount, BigDecimal amount);
}
