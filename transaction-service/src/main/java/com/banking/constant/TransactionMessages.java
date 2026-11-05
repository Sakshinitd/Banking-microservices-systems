package com.banking.constant;

public class TransactionMessages {

    // Debit
    public static final String DEBIT_SUCCESS =
            "Amount debited successfully from your account.";

    // Credit
    public static final String CREDIT_SUCCESS =
            "Amount credited successfully to your account.";

    // Transfer
    public static final String TRANSFER_SUCCESS =
            "Funds transferred successfully.";

    // Reversal
    public static final String REVERSAL_SUCCESS =
            "Transaction has been reversed successfully.";

    // Debit Failures
    public static final String DEBIT_FAILED =
            "Debit transaction failed.  Failure reason: ";

    // Credit Failures
    public static final String CREDIT_FAILED =
            "Credit transaction failed.  Failure reason: ";

    // Transfer Failures
    public static final String TRANSFER_FAILED =
            "Funds transfer failed. Failure reason: ";

    // Reversal Failures
    public static final String REVERSAL_FAILED =
            "Transaction reversal failed.  Failure reason: ";

    public static final String REVERSAL_NOT_ALLOWED =
            "This transaction cannot be reversed.";


}

