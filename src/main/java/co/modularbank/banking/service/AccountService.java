package co.modularbank.banking.service;

import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.controller.error.AccountException;

public interface AccountService {
    public AccountResponse getAccountById(long id) throws AccountException;
    public AccountResponse createAccount(AccountRequest request) throws AccountException;
}