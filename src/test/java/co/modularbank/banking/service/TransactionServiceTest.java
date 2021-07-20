package co.modularbank.banking.service;

import co.modularbank.banking.controller.error.TransactionException;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.domain.Customer;
import co.modularbank.banking.domain.TransactionDirection;
import co.modularbank.banking.mapper.CustomerMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    private long customerId;
    private long accountId;

    @BeforeEach
    void setup() {
        Customer newCustomer = new Customer("Balance", "Mapper", UUID.randomUUID() + "@gmail.com", "11223344");
        customerId = customerMapper.insertCustomer(newCustomer);

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCustomerId(customerId);
        accountRequest.setCountryCode("BD");
        Set<String> currencyList = new HashSet<>();
        currencyList.add("USD");
        currencyList.add("EUR");

        accountRequest.setCurrencyCodes(currencyList);

        Assertions.assertDoesNotThrow(() -> {
            accountId = accountService.createAccount(accountRequest).getAccountId();
        });
    }

    private TransactionRequest getTransactionRequest(long tempAccountId) {
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(tempAccountId);
        request.setAmount(2.2);
        request.setCurrency("USD");
        request.setDirection(TransactionDirection.IN.name());
        request.setDescription("Transaction #In -> $2.2");
        return request;
    }

    @Test
    void givenTransactionRequest_whenMakeTransaction_thenCheckSuccess() {
        TransactionRequest request = getTransactionRequest(accountId);

        Assertions.assertDoesNotThrow(()-> {
            SaveTransactionResponse response = transactionService.makeTransaction(request);

            Assertions.assertEquals(2.2, response.getBalance(), 0.0001);
            Assertions.assertEquals(2.2, response.getAmount(), 0.0001);
            Assertions.assertEquals("USD", response.getCurrency());
            Assertions.assertTrue(response.getTransactionId() > 0);
        });
    }

    @Test
    void givenMultipleTransactionRequest_whenMakeTransaction_thenCheckSuccess() {
        TransactionRequest request = getTransactionRequest(accountId);

        Assertions.assertDoesNotThrow(()-> {
            SaveTransactionResponse response = transactionService.makeTransaction(request);
            Assertions.assertEquals(2.2, response.getBalance(), 0.0001);

            response = transactionService.makeTransaction(request);
            Assertions.assertEquals(4.4, response.getBalance(), 0.0001);

            response = transactionService.makeTransaction(request);
            Assertions.assertEquals(6.6, response.getBalance(), 0.0001);

            request.setDirection(TransactionDirection.OUT.name());
            response = transactionService.makeTransaction(request);
            Assertions.assertEquals(4.4, response.getBalance(), 0.0001);
        });
    }

    @Test
    void givenInvalidAccountId_whenMakeTransaction_thenCheckFailed() {
        TransactionRequest request = getTransactionRequest(9999);
        TransactionException exception = Assertions.assertThrows(TransactionException.class, ()-> {
            transactionService.makeTransaction(request);
        });
        Assertions.assertEquals("Account missing", exception.getMessage());
    }

    @Test
    void givenInvalidCurrencyId_whenMakeTransaction_thenCheckFailed() {
        TransactionRequest request = getTransactionRequest(accountId);
        request.setCurrency("ABC");

        TransactionException exception = Assertions.assertThrows(TransactionException.class, ()-> {
            transactionService.makeTransaction(request);
        });
        Assertions.assertEquals("Invalid currency", exception.getMessage());
    }

    @Test
    void givenInActiveCurrencyId_whenMakeTransaction_thenCheckFailed() {
        TransactionRequest request = getTransactionRequest(accountId);
        request.setCurrency("GBP");

        TransactionException exception = Assertions.assertThrows(TransactionException.class, ()-> {
            transactionService.makeTransaction(request);
        });
        Assertions.assertEquals("Currency not available for this account", exception.getMessage());
    }

    @Test
    void givenLargeOUTAmountThenBalance_whenMakeTransaction_thenCheckFailed() {
        TransactionRequest request = getTransactionRequest(accountId);
        request.setAmount(5);
        request.setDirection(TransactionDirection.OUT.name());

        TransactionException exception = Assertions.assertThrows(TransactionException.class, ()-> {
            transactionService.makeTransaction(request);
        });
        Assertions.assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    void givenAccountId_whenRequestAllTransaction_thenCheckCorrect() {
        TransactionRequest request = getTransactionRequest(accountId);

        Assertions.assertDoesNotThrow(()-> {
            transactionService.makeTransaction(request);
            transactionService.makeTransaction(request);
            transactionService.makeTransaction(request);

            request.setDirection(TransactionDirection.OUT.name());
            transactionService.makeTransaction(request);

            List<TransactionResponse> transactionList = transactionService.getTransactionsForAccount(accountId);
            Assertions.assertEquals(4, transactionList.size());
            Assertions.assertEquals(TransactionDirection.OUT.name(), transactionList.get(0).getDirection());
            Assertions.assertEquals(2.2, transactionList.get(0).getAmount(), 0.001);
        });
    }
    @Test
    void givenInvalidAccountId_whenRequestAllTransaction_thenCheckFailed() {
        TransactionException exception = Assertions.assertThrows(TransactionException.class, ()-> {
            transactionService.getTransactionsForAccount(9999);
        });

        Assertions.assertEquals("Invalid account", exception.getMessage());
    }

    @AfterEach
    void teardown() {
        if(customerId > 0) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
