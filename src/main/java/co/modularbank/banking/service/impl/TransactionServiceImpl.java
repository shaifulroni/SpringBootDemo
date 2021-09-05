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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private TransactionMapper transactionMapper;
    private CurrencyMapper currencyMapper;
    private BalanceMapper balanceMapper;
    private AccountMapper accountMapper;
    private RabbitService rabbitService;

    public TransactionServiceImpl(TransactionMapper transactionMapper,
                                  CurrencyMapper currencyMapper,
                                  BalanceMapper balanceMapper,
                                  AccountMapper accountMapper,
                                  RabbitService rabbitService) {
        this.transactionMapper = transactionMapper;
        this.currencyMapper = currencyMapper;
        this.balanceMapper = balanceMapper;
        this.accountMapper = accountMapper;
        this.rabbitService = rabbitService;
    }

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

        BigDecimal amountChange = transaction.getAmount();
        if(transaction.getDirection() == TransactionDirection.OUT) {
            amountChange = amountChange.negate();
        }
        BigDecimal updatedBalance = currentBalance.getAmount().add(amountChange);

        if(updatedBalance.compareTo(BigDecimal.ZERO) < 0)
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
    private long saveTransaction(Transaction transaction, BigDecimal updatedBalance) {
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
