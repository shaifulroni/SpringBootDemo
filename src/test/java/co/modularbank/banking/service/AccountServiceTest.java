package co.modularbank.banking.service;

import co.modularbank.banking.amqp.message.BaseRabbitMessage;
import co.modularbank.banking.controller.error.AccountException;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.domain.Customer;
import co.modularbank.banking.mapper.CustomerMapper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
public class AccountServiceTest {
    private CustomerMapper customerMapper;
    private AccountService accountService;

    private long customerId;

    @MockBean
    private RabbitService rabbitService;

    @Autowired
    public AccountServiceTest(CustomerMapper customerMapper,
                              AccountService accountService) {
        this.customerMapper = customerMapper;
        this.accountService = accountService;
    }

    @BeforeEach
    void setup() {
        Customer newCustomer = new Customer("FirstName", "LastName", UUID.randomUUID() + "@email.com", "8801231234");
        customerId = customerMapper.insertCustomer(newCustomer);
    }

    private AccountRequest createAccountRequest(long newCustomerId) {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCustomerId(newCustomerId);
        accountRequest.setCountryCode("BD");
        Set<String> currencyList = new HashSet<>();
        currencyList.add("USD");
        currencyList.add("EUR");

        accountRequest.setCurrencyCodes(currencyList);
        return accountRequest;
    }

    @Test
    void givenAccountId_whenGetAccountById_thenVerifyAccountExists() {
        AccountRequest accountRequest = createAccountRequest(customerId);

        Assertions.assertDoesNotThrow(() -> {
            long accountId = accountService.createAccount(accountRequest).getAccountId();

            Assertions.assertDoesNotThrow(() -> {
                AccountResponse existingAccount = accountService.getAccountById(accountId);

                Assertions.assertEquals(customerId, existingAccount.getCustomerId());
                Assertions.assertEquals(2, existingAccount.getBalance().size());
                MatcherAssert.assertThat(existingAccount.getBalance().get(0).getAmount(), Matchers.comparesEqualTo(BigDecimal.ZERO));
            });
        });
    }

    @Test
    void givenInvalidAccountId_whenGetAccountById_thenVerifyAccountExists() {
        AccountException exception = Assertions.assertThrows(AccountException.class, () -> {
            AccountResponse existingAccount = accountService.getAccountById(99999);
        });

        Assertions.assertEquals("No account found", exception.getMessage());
    }

    @Test
    void givenCustomerCountryBalance_whenInsertAccount_thenVerifyAccountCreated() {
        AccountRequest accountRequest = createAccountRequest(customerId);

        Assertions.assertDoesNotThrow(() -> {
            AccountResponse createdAccount = accountService.createAccount(accountRequest);

            Assertions.assertEquals(customerId, createdAccount.getCustomerId());
            Assertions.assertEquals(2, createdAccount.getBalance().size());
            MatcherAssert.assertThat(createdAccount.getBalance().get(0).getAmount(), Matchers.comparesEqualTo(BigDecimal.ZERO));
        });
    }

    @Test
    void givenInvalidCustomer_whenInsertAccount_thenCheckFailed() {
        AccountRequest accountRequest = createAccountRequest(9999);

        AccountException exception = Assertions.assertThrows(AccountException.class, ()-> {
            accountService.createAccount(accountRequest);
        });

        Assertions.assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void givenExistingAccount_whenInsertAccount_thenCheckFailed() {
        AccountRequest accountRequest = createAccountRequest(customerId);

        Assertions.assertDoesNotThrow(() -> {
            AccountResponse createdAccount = accountService.createAccount(accountRequest);
        });

        AccountException exception = Assertions.assertThrows(AccountException.class, ()-> {
            accountService.createAccount(accountRequest);
        });

        Assertions.assertTrue(exception.getMessage().contains("Account exists"));
    }

    @Test
    void givenInvalidCountry_whenInsertAccount_thenCheckFailed() {
        AccountRequest accountRequest = createAccountRequest(customerId);
        accountRequest.setCountryCode("XY");

        AccountException exception = Assertions.assertThrows(AccountException.class, ()-> {
            accountService.createAccount(accountRequest);
        });

        Assertions.assertEquals("Country is not supported", exception.getMessage());
    }

    @Test
    void givenInvalidCurrency_whenInsertAccount_thenCheckFailed() {
        AccountRequest accountRequest = createAccountRequest(customerId);
        Set<String> currency = new HashSet<>();
        currency.add("USD");
        currency.add("XYZ");
        accountRequest.setCurrencyCodes(currency);

        AccountException exception = Assertions.assertThrows(AccountException.class, ()-> {
            accountService.createAccount(accountRequest);
        });

        Assertions.assertEquals("Invalid or unsupported currency", exception.getMessage());
    }

    @AfterEach
    void teardown() {
        if(customerId > 0) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
