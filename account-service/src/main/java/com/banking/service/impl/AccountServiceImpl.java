package com.banking.service.impl;

import com.banking.constant.AccountMessages;
import com.banking.constant.AccountStatus;
import com.banking.dto.AccountCrtReqDto;
import com.banking.dto.AccountKycReqDto;
import com.banking.entity.Account;
import com.banking.exception.AccountBalanceException;
import com.banking.exception.AccountNotFoundException;
import com.banking.exception.AccountStatusException;
import com.banking.messaging.event.AccountEvent;
import com.banking.messaging.producer.AccountEventProducer;
import com.banking.repository.AccountRepository;
import com.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;

import static com.banking.mapper.AccountMapper.mapAccountCrtReqDtoToAccount;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountEventProducer accountEventProducer;


    @Override
    @CacheEvict(value = "accountsByUser", key = "'user:' + #p0")
    public Account createAccount(AccountCrtReqDto accountCrtReqDto, String email) {
        Account newAccount = mapAccountCrtReqDtoToAccount(accountCrtReqDto);
        newAccount.setAccountNumber(generateAccountNumber());
        accountEventProducer.produceAccountEvent(new AccountEvent(email, AccountMessages.ACCOUNT_CREATED_SUCCESS, LocalDateTime.now()));
        return accountRepository.save(newAccount);
    }

    @Override
    @Cacheable(value = "account", key = "'account:' + #accountNumber")
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new AccountNotFoundException("Account not found!"));
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        Account existingAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountNotFoundException("Account not found!"));
        return existingAccount.getBalance();
    }

    @Override
    @CacheEvict(value = "account", key = "'account:' + #accountId")
    public Account updateAccountKycDetails(UUID accountId, AccountKycReqDto accountKycReqDto, String email) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException("Account not found!"));
        existingAccount.setMobileNumber(accountKycReqDto.getMobileNumber());
        existingAccount.setPinCode(accountKycReqDto.getPinCode());
        existingAccount.setAddress(accountKycReqDto.getAddress());
        existingAccount.setKycStatus(accountKycReqDto.getKycStatus());
        existingAccount.setKycUpdatedAt(LocalDateTime.now());
        accountEventProducer.produceAccountEvent(new AccountEvent(email, AccountMessages.KYC_UPDATED_SUCCESS, LocalDateTime.now()));
        return accountRepository.save(existingAccount);
    }

    @Override
    @CacheEvict(value = "account", key = "'account:' + #accountId")
    public Account freezeAccount(UUID accountId, String email) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException("Account not found!"));
        existingAccount.setStatus(AccountStatus.FROZEN);
        accountEventProducer.produceAccountEvent(new AccountEvent(email, AccountMessages.ACCOUNT_FROZEN_SUCCESS, LocalDateTime.now()));
        return accountRepository.save(existingAccount);
    }

    @Override
    public List<Account> getAllAccountsOfCustomer(UUID userId) {
        return accountRepository.findAllByUserId(userId);
    }

    @Override
    public String debit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new AccountNotFoundException("Account not found!"));

        if(account.getStatus().equals(AccountStatus.FROZEN)){
            throw new AccountStatusException("Account is frozen. Cannot debit money.");
        }
        if(account.getBalance().compareTo(amount) < 0){

            throw new AccountBalanceException("Debit amount cannot be greater than account balance.");
        }
        BigDecimal balance = account.getBalance().subtract(amount);
        account.setBalance(balance);
        accountRepository.save(account);
        return "Account debited Rs."+amount+" successfully. Available balance is Rs."+balance;
    }

    @Override
    public String credit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new AccountNotFoundException("Account not found!"));

        if(account.getStatus().equals(AccountStatus.FROZEN)){
            throw new AccountStatusException("Account is frozen. Cannot credit money.");
        }

        BigDecimal balance = account.getBalance().add(amount);
        account.setBalance(balance);
        accountRepository.save(account);
        return "Account credited Rs."+amount+" successfully. Available balance is Rs."+balance;
    }

    @Override
    public String transfer(String fromAccount, String toAccount, BigDecimal amount) {
        Account debitAccount = accountRepository.findByAccountNumber(fromAccount).orElseThrow(()-> new AccountNotFoundException("Debitor account not found!"));
        Account creditAccount = accountRepository.findByAccountNumber(toAccount).orElseThrow(()-> new AccountNotFoundException("Creditor account not found!"));

        if(debitAccount.getStatus().equals(AccountStatus.FROZEN)){
            throw new AccountStatusException("Account is frozen. Cannot debit money.");
        }
        if(creditAccount.getStatus().equals(AccountStatus.FROZEN)){
            throw new AccountStatusException("Account is frozen. Cannot credit money.");
        }
        if(debitAccount.getBalance().compareTo(amount) < 0){
            throw new AccountBalanceException("Debit amount cannot be greater than account balance.");
        }
        BigDecimal debitBalance = debitAccount.getBalance().subtract(amount);
        debitAccount.setBalance(debitBalance);
        accountRepository.save(debitAccount);

        BigDecimal creditBalance = creditAccount.getBalance().add(amount);
        creditAccount.setBalance(creditBalance);
        accountRepository.save(creditAccount);

        return "Transaction completed successfully.";
    }

    public String generateAccountNumber() {
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(900) + 100;
        return String.valueOf(timestamp) + random;
    }
}
