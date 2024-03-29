package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.AccountException;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(
            @PathVariable long id
    ) throws AccountException {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) throws AccountException {
        return new ResponseEntity<>(accountService.createAccount(request), HttpStatus.CREATED);
    }
}
