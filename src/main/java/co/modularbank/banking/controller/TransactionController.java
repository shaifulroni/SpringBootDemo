package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.TransactionException;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<SaveTransactionResponse> makeTransaction(
            @Valid @RequestBody TransactionRequest request
    ) throws TransactionException {
        return new ResponseEntity<>(transactionService.makeTransaction(request), HttpStatus.CREATED);
    }

    @GetMapping("/account/{id}")
    public List<TransactionResponse> getTransactionList(@PathVariable("id") long accountId) throws TransactionException {
        return transactionService.getTransactionsForAccount(accountId);
    }
}
