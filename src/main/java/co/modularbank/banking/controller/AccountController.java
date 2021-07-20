package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.AccountException;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    public AccountResponse getAccount(
            @PathVariable long id
    ) throws AccountException {
        return accountService.getAccountById(id);
    }

    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody AccountRequest request) throws AccountException {
        return accountService.createAccount(request);
    }
}
