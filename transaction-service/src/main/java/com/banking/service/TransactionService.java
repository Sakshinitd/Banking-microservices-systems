package com.banking.service;

import com.banking.dto.request.CreditRequest;
import com.banking.dto.request.DebitRequest;
import com.banking.dto.request.TransferRequest;
import com.banking.dto.response.TransactionResponse;
import com.banking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

    TransactionResponse debit(DebitRequest debitRequest,String email);
    TransactionResponse credit(CreditRequest creditRequest, String email);
    TransactionResponse transfer(TransferRequest transferRequest, String email);
    TransactionResponse reversal(String referenceId,String email);
    Page<Transaction> getTransactions(String accountNumber, Pageable pageable);
}
