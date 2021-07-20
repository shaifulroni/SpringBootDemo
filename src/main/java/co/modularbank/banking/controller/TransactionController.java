package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.TransactionException;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping
    public SaveTransactionResponse makeTransaction(
            @Valid @RequestBody TransactionRequest request
    ) throws TransactionException {
        return transactionService.makeTransaction(request);
    }

    @GetMapping("/account/{id}")
    public List<TransactionResponse> getTransactionList(@PathVariable("id") long accountId) throws TransactionException {
        return transactionService.getTransactionsForAccount(accountId);
    }
}
