package co.modularbank.banking.service;

import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.error.TransactionException;
import co.modularbank.banking.domain.Transaction;

import java.util.List;

public interface TransactionService {
    public SaveTransactionResponse makeTransaction(TransactionRequest transaction) throws TransactionException;
    public List<TransactionResponse> getTransactionsForAccount(long account) throws TransactionException;
}
