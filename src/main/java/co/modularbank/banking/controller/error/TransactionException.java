package co.modularbank.banking.controller.error;

public class TransactionException extends Exception {
    public TransactionException(String message) {
        super(message);
    }
}
