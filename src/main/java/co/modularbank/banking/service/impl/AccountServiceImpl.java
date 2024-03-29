package co.modularbank.banking.service.impl;

import co.modularbank.banking.amqp.message.AccountMessage;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.controller.error.AccountException;
import co.modularbank.banking.domain.*;
import co.modularbank.banking.mapper.*;
import co.modularbank.banking.service.AccountService;
import co.modularbank.banking.service.RabbitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    private final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private CountryMapper countryMapper;
    private AccountMapper accountMapper;
    private BalanceMapper balanceMapper;
    private CurrencyMapper currencyMapper;
    private CustomerMapper customerMapper;

    private RabbitService rabbitService;

    public AccountServiceImpl(CountryMapper countryMapper,
                              AccountMapper accountMapper,
                              BalanceMapper balanceMapper,
                              CurrencyMapper currencyMapper,
                              CustomerMapper customerMapper,
                              RabbitService rabbitService) {
        this.countryMapper = countryMapper;
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.currencyMapper = currencyMapper;
        this.customerMapper = customerMapper;
        this.rabbitService = rabbitService;
    }

    public AccountResponse getAccountById(long id) throws AccountException{
        Optional<Account> account = accountMapper.getAccountById(id);
        if(account.isEmpty()) throw new AccountException("No account found");
        return account.get().toAccountResponse();
    }

    public AccountResponse createAccount(AccountRequest request) throws AccountException {
        Customer customer = customerMapper.getCustomerById(request.getCustomerId())
                .orElseThrow(() -> new AccountException("Customer not found"));
        logger.debug("Customer - " + customer);

        Optional<Account> existingAccount = accountMapper.getAccountByCustomerId(customer.getId());
        if(existingAccount.isPresent()) throw new AccountException("Account exists for this customer");
        logger.debug("Existing account not found");

        Country country = countryMapper.getCountryByCodeName(request.getCountryCode())
                .orElseThrow(()-> new AccountException("Country is not supported"));
        logger.debug("Country - " + country);

        List<Currency> currencyList = request.getCurrencyCodes()
                .stream()
                .map( currencyCode->
                    currencyMapper.getCurrencyByShortName(currencyCode)
                            .orElse(null)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if(currencyList.size() != request.getCurrencyCodes().size()) {
            throw new AccountException("Invalid or unsupported currency");
        }
        logger.debug("Currency - " + currencyList);

        AccountResponse response = saveAccount(customer, country, currencyList)
                .map(Account::toAccountResponse)
                .orElseThrow(()-> new AccountException("Failed to create account"));

        rabbitService.sendMessage(new AccountMessage(
                "Account created with id " + response.getAccountId(),
                response
        ));
        return response;
    }

    @Transactional
    Optional<Account> saveAccount(Customer customer, Country country, List<Currency> currencyList) {
        Account account = new Account();
        account.setCountry(country);
        account.setCustomer(customer);

        logger.debug("Account - " + account);

        long accountId = accountMapper.insertAccount(account);
        currencyList.forEach(currency -> {
            Balance balance = new Balance();
            balance.setCurrency(currency);
            balance.setAmount(BigDecimal.ZERO);
            balanceMapper.addBalanceToAccount(accountId, balance);
        });
        return accountMapper.getAccountById(accountId);
    }
}
