package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class TransactionMapperTest {
    @Autowired TransactionMapper transactionMapper;
    @Autowired CurrencyMapper currencyMapper;
    @Autowired CustomerMapper customerMapper;
    @Autowired AccountMapper accountMapper;
    @Autowired CountryMapper countryMapper;

    private Customer testCustomer;
    private Account testAccount;
    private Country testCountry;

    @BeforeEach
    void setup() {
        Customer newCustomer = new Customer("Balance", "Mapper", UUID.randomUUID() + "@gmail.com", "11223344");
        long customerId = customerMapper.insertCustomer(newCustomer);

        testCustomer = customerMapper.getCustomerById(customerId).orElseThrow();

        testCountry = countryMapper.getCountryByCodeName("EE").orElseThrow();
        Account newAccount = new Account();
        newAccount.setCustomer(testCustomer);
        newAccount.setCountry(testCountry);
        long accountId = accountMapper.insertAccount(newAccount);

        testAccount = accountMapper.getAccountById(accountId).orElseThrow();
    }

    @Test
    void testInsertTransactionForAccountAndGet() {
        Transaction transaction = new Transaction();
        transaction.setAccountId(testAccount.getId());
        transaction.setCurrency(currencyMapper.getCurrencyByShortName("USD").orElseThrow());
        transaction.setAmount(55);
        transaction.setDirection(TransactionDirection.IN);
        transaction.setDescription("Test transaction 1");

        long transactionId = transactionMapper.insertTransaction(transaction);
        Assertions.assertTrue(transactionId > 0);

        List<Transaction> transactionList = transactionMapper.getTransactionsByAccountId (testAccount.getId());
        Assertions.assertEquals(1, transactionList.size());

        Assertions.assertEquals("USD", transactionList.get(0).getCurrency().getShortName());
        Assertions.assertEquals(55.0, transactionList.get(0).getAmount());
        Assertions.assertEquals(TransactionDirection.IN, transactionList.get(0).getDirection());
    }

    @Test
    void testInsertTransactionToInvalidAccount() {
        Transaction transaction = new Transaction();
        transaction.setAccountId(9999);
        transaction.setCurrency(currencyMapper.getCurrencyByShortName("USD").orElseThrow());
        transaction.setAmount(55);
        transaction.setDirection(TransactionDirection.OUT);
        transaction.setDescription("Test transaction 1");

        Assertions.assertThrows(Exception.class, ()-> transactionMapper.insertTransaction(transaction));
    }

    @Test
    void testInsertTransactionWithInvalidCurrency() {
        Transaction transaction = new Transaction();
        transaction.setAccountId(testAccount.getId());
        transaction.setCurrency(currencyMapper.getCurrencyByShortName("USD").orElseThrow());
        transaction.setAmount(-55);
        transaction.setDirection(TransactionDirection.OUT);
        transaction.setDescription("Test transaction 1");

        Assertions.assertThrows(Exception.class, ()-> transactionMapper.insertTransaction(transaction));
    }

    @AfterEach
    void teardown() {
        customerMapper.deleteCustomerById(testCustomer.getId());
    }
}
