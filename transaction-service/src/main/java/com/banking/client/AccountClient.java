package com.banking.client;


import com.banking.client.config.AccountFeignClientConfig;
import com.banking.dto.response.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "account-service",configuration = AccountFeignClientConfig.class)
public interface AccountClient {

    @GetMapping("/api/v1/accounts/{accountNumber}/balance")
    BigDecimal getAccountBalance(@PathVariable String accountNumber);

    @PostMapping("/api/v1/accounts/{accountNumber}/credit/{amount}")
    ResponseEntity<String> deposit(@PathVariable String accountNumber, @PathVariable BigDecimal amount);

    @PostMapping("/api/v1/accounts/{accountNumber}/debit/{amount}")
    ResponseEntity<String> withdraw(@PathVariable String accountNumber, @PathVariable BigDecimal amount);

    @PostMapping("/api/v1/accounts/{fromAccount}/transfer/{toAccount}/{amount}")
    ResponseEntity<String> transfer(@PathVariable String fromAccount,@PathVariable String toAccount, @PathVariable BigDecimal amount);
}
