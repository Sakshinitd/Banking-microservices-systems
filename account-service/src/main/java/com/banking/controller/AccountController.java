package com.banking.controller;

import com.banking.dto.AccountCrtReqDto;
import com.banking.dto.AccountKycReqDto;
import com.banking.entity.Account;
import com.banking.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountCrtReqDto accountCrtReqDto,
                                                 @RequestHeader("X-User-Email") String email) {
        return new ResponseEntity<>(
                accountService.createAccount(accountCrtReqDto,email),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccountDetails(@PathVariable String accountNumber) {
        return new ResponseEntity<>(
                accountService.findByAccountNumber(accountNumber),
                HttpStatus.OK
        );
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable String accountNumber) {
        return new ResponseEntity<>(
                accountService.getBalance(accountNumber),
                HttpStatus.OK
        );
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<Account> updateAccountKycDetails(@PathVariable UUID accountId,
                                                           @Valid @RequestBody AccountKycReqDto accountKycReqDto,
                                                           @RequestHeader("X-User-Email") String email) {
        return new ResponseEntity<>(
                accountService.updateAccountKycDetails(accountId, accountKycReqDto,email),
                HttpStatus.ACCEPTED
        );
    }

    @PutMapping("/{accountId}/freeze")
    public ResponseEntity<Account> freezeAccount(@PathVariable UUID accountId,
                                                 @RequestHeader("X-User-Email") String email) {
        return new ResponseEntity<>(
                accountService.freezeAccount(accountId,email),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Account>> getAllAccountsOfCustomer(@PathVariable UUID customerId) {
        return new ResponseEntity<>(
                accountService.getAllAccountsOfCustomer(customerId),
                HttpStatus.OK
        );
    }

    @PostMapping("/{accountNumber}/debit/{amount}")
    public ResponseEntity<String> debit(@PathVariable String accountNumber,
                                        @PathVariable BigDecimal amount) {
        return new ResponseEntity<>(
                accountService.debit(accountNumber,amount),
                HttpStatus.ACCEPTED
        );
    }
    @PostMapping("/{accountNumber}/credit/{amount}")
    public ResponseEntity<String> credit(@PathVariable String accountNumber,
                                         @PathVariable BigDecimal amount) {
        return new ResponseEntity<>(
                accountService.credit(accountNumber,amount),
                HttpStatus.ACCEPTED
        );
    }

    @PostMapping("/{fromAccount}/transfer/{toAccount}/{amount}")
    public ResponseEntity<String> credit(@PathVariable String fromAccount,
                                         @PathVariable String toAccount,
                                         @PathVariable BigDecimal amount) {
        return new ResponseEntity<>(
                accountService.transfer(fromAccount,toAccount,amount),
                HttpStatus.ACCEPTED
        );
    }



}
