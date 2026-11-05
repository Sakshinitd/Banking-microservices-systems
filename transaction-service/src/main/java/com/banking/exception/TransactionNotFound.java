package com.banking.exception;

public class TransactionNotFound extends RuntimeException{

    public TransactionNotFound(String message) {
        super(message);
    }
}
