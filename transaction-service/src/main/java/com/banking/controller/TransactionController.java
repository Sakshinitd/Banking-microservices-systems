package com.banking.controller;

import com.banking.dto.request.CreditRequest;
import com.banking.dto.request.DebitRequest;
import com.banking.dto.request.TransferRequest;
import com.banking.dto.response.TransactionResponse;
import com.banking.entity.Transaction;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/debit")
    public ResponseEntity<TransactionResponse> debit(@Valid @RequestBody DebitRequest debitRequest,
                                                     @RequestHeader("X-User-Email") String email) {
         return new  ResponseEntity<>(
                 transactionService.debit(debitRequest,email),
                 HttpStatus.ACCEPTED
                 );
    }

    @PostMapping("/credit")
    public ResponseEntity<TransactionResponse> credit(@Valid @RequestBody CreditRequest creditRequest,
                                                      @RequestHeader("X-User-Email") String email) {
        return new  ResponseEntity<>(
                transactionService.credit(creditRequest,email),
                HttpStatus.ACCEPTED
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest transferRequest,
                                                        @RequestHeader("X-User-Email") String email) {
        return new  ResponseEntity<>(
                transactionService.transfer(transferRequest,email),
                HttpStatus.ACCEPTED
        );
    }

    @PostMapping("/{referenceId}/reversal")
    public ResponseEntity<TransactionResponse> reversal(@PathVariable String referenceId,
                                                        @RequestHeader("X-User-Email") String email) {
        return new  ResponseEntity<>(
                transactionService.reversal(referenceId,email),
                HttpStatus.ACCEPTED
        );
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Page<Transaction>> getTransactions(@PathVariable String accountNumber, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return new  ResponseEntity<>(
                transactionService.getTransactions(accountNumber,pageable),
                HttpStatus.OK
        );
    }
}
