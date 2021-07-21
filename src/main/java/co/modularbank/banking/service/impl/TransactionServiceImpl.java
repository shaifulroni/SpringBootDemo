package co.modularbank.banking.service.impl;

import co.modularbank.banking.amqp.message.TransactionMessage;
import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.error.TransactionException;
import co.modularbank.banking.domain.Account;
import co.modularbank.banking.domain.Balance;
import co.modularbank.banking.domain.Transaction;
import co.modularbank.banking.domain.TransactionDirection;
import co.modularbank.banking.mapper.AccountMapper;
import co.modularbank.banking.mapper.BalanceMapper;
import co.modularbank.banking.mapper.CurrencyMapper;
import co.modularbank.banking.mapper.TransactionMapper;
import co.modularbank.banking.service.RabbitService;
import co.modularbank.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private CurrencyMapper currencyMapper;

    @Autowired
    private BalanceMapper balanceMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private RabbitService rabbitService;

    public SaveTransactionResponse makeTransaction(TransactionRequest transactionRequest) throws TransactionException {
        Transaction transaction = new Transaction();
        transaction.setAccountId(transactionRequest.getAccountId());
        transaction.setDirection(TransactionDirection.valueOf(transactionRequest.getDirection()));
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setCurrency(currencyMapper.getCurrencyByShortName(transactionRequest.getCurrency())
                .orElseThrow(()-> new TransactionException("Invalid currency")));
        transaction.setDescription(transactionRequest.getDescription());

        Account account = accountMapper.getAccountById(transaction.getAccountId())
                .orElseThrow(() -> new TransactionException("Account missing"));

        Balance currentBalance = balanceMapper.getAccountBalanceWithCurrency(transaction.getAccountId(), transaction.getCurrency().getId())
                .orElseThrow(() -> new TransactionException("Currency not available for this account"));

        double amountChange = transaction.getAmount();
        if(transaction.getDirection() == TransactionDirection.OUT) {
            amountChange = -amountChange;
        }
        double updatedBalance = currentBalance.getAmount() + amountChange;

        if(updatedBalance < 0.0)
            throw new TransactionException("Insufficient funds");

        long transactionId = saveTransaction(transaction, updatedBalance);
        SaveTransactionResponse response = new SaveTransactionResponse(
                transaction.getAccountId(),
                transactionId,
                transaction.getAmount(),
                transaction.getCurrency().getShortName(),
                transaction.getDirection().name(),
                transaction.getDescription(),
                updatedBalance
        );

        rabbitService.sendMessage(new TransactionMessage(
                "Transaction created for account " +
                        response.getAccountId() +
                        " with id " +
                        response.getTransactionId(),
                response
        ));

        return response;
    }

    @Transactional
    private long saveTransaction(Transaction transaction, double updatedBalance) {
        balanceMapper.updateAccountBalanceWithCurrency(transaction.getAccountId(), transaction.getCurrency().getId(), updatedBalance);
        return transactionMapper.insertTransaction(transaction);
    }

    public List<TransactionResponse> getTransactionsForAccount(long accountId) throws TransactionException {
        Account account = accountMapper.getAccountById(accountId)
                .orElseThrow(() -> new TransactionException("Invalid account"));

        return transactionMapper.getTransactionsByAccountId(accountId).stream().map( transaction ->
                new TransactionResponse(transaction.getAccountId(),
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getCurrency().getShortName(),
                        transaction.getDirection().name(),
                        transaction.getDescription()
                )
        ).collect(Collectors.toList());
    }
}
