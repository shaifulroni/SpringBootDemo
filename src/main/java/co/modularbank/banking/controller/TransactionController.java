package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.TransactionException;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.domain.Transaction;
import co.modularbank.banking.domain.TransactionDirection;
import co.modularbank.banking.mapper.CurrencyMapper;
import co.modularbank.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    CurrencyMapper currencyMapper;

    @Autowired
    TransactionService transactionService;

    @PostMapping
    public SaveTransactionResponse makeTransaction(
            @Valid @RequestBody TransactionRequest request
    ) throws TransactionException {
        Transaction transaction = new Transaction();
        transaction.setAccountId(request.getAccountId());
        transaction.setDirection(TransactionDirection.valueOf(request.getDirection()));
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(currencyMapper.getCurrencyByShortName(request.getCurrency())
                .orElseThrow(()-> new TransactionException("Invalid currency")));
        transaction.setDescription(request.getDescription());
        return transactionService.makeTransaction(transaction);
    }

    @GetMapping("/account/{id}")
    public List<TransactionResponse> getTransactionList(@PathVariable("id") long accountId) throws TransactionException {
        return transactionService.getTransactionsForAccount(accountId);
    }
}
