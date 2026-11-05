package com.banking.service.impl;

import com.banking.client.AccountClient;
import com.banking.constant.TransactionMessages;
import com.banking.constant.TransactionStatus;
import com.banking.dto.request.CreditRequest;
import com.banking.dto.request.DebitRequest;
import com.banking.dto.request.TransferRequest;
import com.banking.dto.response.TransactionResponse;
import com.banking.entity.Transaction;
import com.banking.exception.TransactionNotFound;
import com.banking.messaging.event.TransactionEvent;
import com.banking.messaging.producer.TransactionEventProducer;
import com.banking.repository.TransactionRepository;
import com.banking.service.IdempotencyService;
import com.banking.service.TransactionService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final IdempotencyService idempotencyService;
    private static final String LOCK_PREFIX = "lock:account:";
    private final StringRedisTemplate redisTemplate;
    private final AccountClient accountClient;
    private final TransactionEventProducer transactionEventProducer;

    @Override
    @Transactional
    public TransactionResponse debit(DebitRequest debitRequest, String email) {

        Optional<TransactionResponse> cached = idempotencyService.get(debitRequest.getIdempotencyKey());
        if (cached.isPresent()) {
            return cached.get();
        }
        String accountNumber = debitRequest.getAccountNumber();
        String lockKey = LOCK_PREFIX + accountNumber;
        acquireAccountLock(lockKey);

        try {
            ResponseEntity<String> result = accountClient.withdraw(debitRequest.getAccountNumber(), debitRequest.getAmount());
            String referenceId = UUID.randomUUID().toString();

            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setReferenceId(referenceId);
            transactionResponse.setStatus(TransactionStatus.PENDING);
            transactionResponse.setTimestamp(LocalDateTime.now());
            idempotencyService.set(debitRequest.getIdempotencyKey(), transactionResponse);

            Transaction transaction = getTransaction(debitRequest, referenceId, result);
            transaction.setTransactionDate(transactionResponse.getTimestamp());
            transactionRepository.save(transaction);

           transactionResponse.setStatus(transaction.getStatus());

            idempotencyService.set(debitRequest.getIdempotencyKey(), transactionResponse);
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.DEBIT_SUCCESS,
                    LocalDateTime.now()
            ));
            return transactionResponse;
        }catch (FeignException ex) {
            TransactionResponse failedResponse = new TransactionResponse(TransactionStatus.FAILED, LocalDateTime.now(), null);
            idempotencyService.set(debitRequest.getIdempotencyKey(),failedResponse);
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.DEBIT_FAILED+ex.getMessage(),
                    LocalDateTime.now()
            ));
            throw ex;
        } finally {
            releaseLock(lockKey);
        }
    }

    private static @NonNull Transaction getTransaction(DebitRequest debitRequest, String referenceId, ResponseEntity<String> result) {
        Transaction transaction = new Transaction();
        transaction.setDebitAccountNumber(debitRequest.getAccountNumber());
        transaction.setAmount(debitRequest.getAmount());
        transaction.setDescription(debitRequest.getDescription());
        transaction.setIdempotencyKey(debitRequest.getIdempotencyKey());
        transaction.setReferenceId(referenceId);
        transaction.setType(debitRequest.getType());
        if (result.getStatusCode().is2xxSuccessful()) {
            transaction.setStatus(TransactionStatus.COMPLETED);
        } else {
            transaction.setFailureReason(result.getBody());
            transaction.setStatus(TransactionStatus.FAILED);
        }
        return transaction;
    }

    @Override
    public TransactionResponse credit(CreditRequest creditRequest, String email) {
        Optional<TransactionResponse> cached = idempotencyService.get(creditRequest.getIdempotencyKey());
        if (cached.isPresent()) {
            return cached.get();
        }
        String accountNumber = creditRequest.getAccountNumber();
        String lockKey = LOCK_PREFIX + accountNumber;
        acquireAccountLock(lockKey);

        try {
            ResponseEntity<String> result = accountClient.deposit(accountNumber, creditRequest.getAmount());
            String referenceId = UUID.randomUUID().toString();

            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setReferenceId(referenceId);
            transactionResponse.setStatus(TransactionStatus.PENDING);
            transactionResponse.setTimestamp(LocalDateTime.now());
            idempotencyService.set(creditRequest.getIdempotencyKey(), transactionResponse);

            Transaction transaction = getTransaction(creditRequest, referenceId, result);
            transaction.setTransactionDate(transactionResponse.getTimestamp());
            transactionRepository.save(transaction);

            transactionResponse.setStatus(transaction.getStatus());

            idempotencyService.set(creditRequest.getIdempotencyKey(), transactionResponse);

            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.CREDIT_SUCCESS,
                    LocalDateTime.now()
            ));

            return transactionResponse;
        }catch (FeignException ex) {
            TransactionResponse failedResponse = new TransactionResponse(TransactionStatus.FAILED, LocalDateTime.now(), null);
            idempotencyService.set(creditRequest.getIdempotencyKey(),failedResponse);
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.CREDIT_FAILED+ex.getMessage(),
                    LocalDateTime.now()
            ));
            throw ex;
        } finally {
            releaseLock(lockKey);
        }
    }

    private static @NonNull Transaction getTransaction(CreditRequest creditRequest, String referenceId, ResponseEntity<String> result) {
        Transaction transaction = new Transaction();
        transaction.setDebitAccountNumber(creditRequest.getAccountNumber());
        transaction.setAmount(creditRequest.getAmount());
        transaction.setDescription(creditRequest.getDescription());
        transaction.setIdempotencyKey(creditRequest.getIdempotencyKey());
        transaction.setReferenceId(referenceId);
        transaction.setType(creditRequest.getType());
        if (result.getStatusCode().is2xxSuccessful()) {
            transaction.setStatus(TransactionStatus.COMPLETED);
        } else {
            transaction.setFailureReason(result.getBody());
            transaction.setStatus(TransactionStatus.FAILED);
        }
        return transaction;
    }

    @Override
    public TransactionResponse transfer(TransferRequest transferRequest, String email) {

        Optional<TransactionResponse> cached = idempotencyService.get(transferRequest.getIdempotencyKey());
        if (cached.isPresent()) {
            return cached.get();
        }

        String fromLock = LOCK_PREFIX + transferRequest.getFromAccount();
        String toLock = LOCK_PREFIX + transferRequest.getToAccount();

        acquireAccountLock(fromLock);
        acquireAccountLock(toLock);

        try {
            String referenceId = UUID.randomUUID().toString();

            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setReferenceId(referenceId);
            transactionResponse.setStatus(TransactionStatus.PENDING);
            transactionResponse.setTimestamp(LocalDateTime.now());
            idempotencyService.set(transferRequest.getIdempotencyKey(), transactionResponse);

            ResponseEntity<String> result = accountClient.transfer(
                            transferRequest.getFromAccount(),
                            transferRequest.getToAccount(),
                            transferRequest.getAmount()
                        );

            Transaction transaction = getTransaction(transferRequest, referenceId, result);
            transaction.setTransactionDate(transactionResponse.getTimestamp());
            transactionRepository.save(transaction);

            transactionResponse.setStatus(transaction.getStatus());

            idempotencyService.set(transferRequest.getIdempotencyKey(),transactionResponse);
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.TRANSFER_SUCCESS,
                    LocalDateTime.now()
            ));
            return transactionResponse;
        } catch (FeignException ex) {
            TransactionResponse failedResponse = new TransactionResponse(TransactionStatus.FAILED, LocalDateTime.now(), null);
            idempotencyService.set(transferRequest.getIdempotencyKey(),failedResponse);
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.TRANSFER_FAILED+ex.getMessage(),
                    LocalDateTime.now()
            ));
            throw ex;
        } finally {
            releaseLock(fromLock);
            releaseLock(toLock);
        }
    }

    private static @NonNull Transaction getTransaction(TransferRequest transferRequest, String referenceId, ResponseEntity<String> result) {
        Transaction transaction = new Transaction();
        transaction.setDebitAccountNumber(transferRequest.getFromAccount());
        transaction.setCreditAccountNumber(transferRequest.getToAccount());
        transaction.setAmount(transferRequest.getAmount());
        transaction.setDescription(transferRequest.getDescription());
        transaction.setIdempotencyKey(transferRequest.getIdempotencyKey());
        transaction.setReferenceId(referenceId);
        transaction.setType(transferRequest.getType());
        if (result.getStatusCode().is2xxSuccessful()) {
            transaction.setStatus(TransactionStatus.COMPLETED);
        } else {
            transaction.setFailureReason(result.getBody());
            transaction.setStatus(TransactionStatus.FAILED);
        }
        return transaction;
    }

    @Override
    public TransactionResponse reversal(String referenceId, String email) {

        Optional<TransactionResponse> cached = idempotencyService.get(referenceId);
        if (cached.isPresent()) {
            return cached.get();
        }

        Transaction transaction = transactionRepository.findByReferenceId(referenceId).orElseThrow(() ->
        {
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.REVERSAL_NOT_ALLOWED,
                    LocalDateTime.now()
            ));
            return new TransactionNotFound("Transaction not found for reversal! " + referenceId);
        });

        String fromAccount = transaction.getCreditAccountNumber();
        String fromAccountLockKey = LOCK_PREFIX + fromAccount;
        acquireAccountLock(fromAccountLockKey);
        String toAccount = transaction.getDebitAccountNumber();
        String toAccountLockKey = LOCK_PREFIX + toAccount;
        acquireAccountLock(toAccountLockKey);

        try {

            TransactionResponse transactionResponse = new TransactionResponse();
            transactionResponse.setReferenceId(referenceId);
            transactionResponse.setStatus(TransactionStatus.PENDING);
            transactionResponse.setTimestamp(LocalDateTime.now());
            idempotencyService.set(transaction.getIdempotencyKey(), transactionResponse);

            ResponseEntity<String> result = accountClient.transfer(fromAccount, toAccount, transaction.getAmount());
            Transaction reversalTransaction = new Transaction();
            reversalTransaction.setDebitAccountNumber(fromAccount);
            reversalTransaction.setCreditAccountNumber(toAccount);
            reversalTransaction.setAmount(transaction.getAmount());
            reversalTransaction.setDescription(transaction.getDescription());
            reversalTransaction.setIdempotencyKey(transaction.getIdempotencyKey());
            reversalTransaction.setReferenceId(referenceId);
            reversalTransaction.setTransactionDate(transactionResponse.getTimestamp());
            if (result.getStatusCode().is2xxSuccessful()) {
                reversalTransaction.setStatus(TransactionStatus.REVERSED);
            } else {
                reversalTransaction.setStatus(TransactionStatus.FAILED);
            }
            transactionRepository.save(reversalTransaction);

            transactionResponse.setStatus(transaction.getStatus());
            idempotencyService.set(referenceId, transactionResponse);
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.REVERSAL_SUCCESS,
                    LocalDateTime.now()
            ));
            return transactionResponse;
        }catch (FeignException ex) {
            TransactionResponse failedResponse = new TransactionResponse(TransactionStatus.FAILED, LocalDateTime.now(), null);
            idempotencyService.set(referenceId, failedResponse);
            transactionEventProducer.publish(new TransactionEvent(
                    email,
                    TransactionMessages.REVERSAL_FAILED+ex.getMessage(),
                    LocalDateTime.now()
            ));
            throw ex;
        }finally {
            releaseLock(fromAccountLockKey);
            releaseLock(toAccountLockKey);
        }
    }

    @Override
    public Page<Transaction> getTransactions(String accountNumber, Pageable pageable) {
        return transactionRepository.findByDebitAccountNumber(accountNumber, pageable);
    }

    private void acquireAccountLock(String lockKey) {
        Boolean success = redisTemplate
                .opsForValue()
                .setIfAbsent(lockKey, "locked", Duration.ofSeconds(5));

        if (!Boolean.TRUE.equals(success)) {
            throw new RuntimeException("Account is busy");
        }
    }

    private void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }

}
