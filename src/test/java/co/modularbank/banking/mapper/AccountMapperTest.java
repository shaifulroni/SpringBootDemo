package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Account;
import co.modularbank.banking.domain.Country;
import co.modularbank.banking.domain.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class AccountMapperTest {
    private AccountMapper accountMapper;
    private CountryMapper countryMapper;
    private CustomerMapper customerMapper;

    private Customer testCustomer;
    private Country testCountry;

    @Autowired
    public AccountMapperTest(AccountMapper accountMapper,
                             CountryMapper countryMapper,
                             CustomerMapper customerMapper) {
        this.accountMapper = accountMapper;
        this.countryMapper = countryMapper;
        this.customerMapper = customerMapper;
    }

    @BeforeEach
    void setup() {
        Customer newCustomer = new Customer("Account", "Mapper", UUID.randomUUID() + "@gmail.com", "11223344");
        long customerId = customerMapper.insertCustomer(newCustomer);

        testCustomer = customerMapper.getCustomerById(customerId).orElseThrow();
        testCountry = countryMapper.getCountryByCodeName("BD").orElseThrow();
    }

    @Test
    void testInsertAccountToInvalidCustomer() {
        Customer invalidCustomer = new Customer();
        invalidCustomer.setId(9999);

        Account account = new Account();
        account.setCountry(testCountry);
        account.setCustomer(invalidCustomer);

        Assertions.assertThrows(Exception.class, ()-> {accountMapper.insertAccount(account);});
    }

    @Test
    void testInsertAndGetAccount() {
        Account account = new Account();
        account.setCustomer(testCustomer);
        account.setCountry(testCountry);

        long accountId = accountMapper.insertAccount(account);
        Assertions.assertTrue(accountId > 0);

        Account createdAccount = accountMapper.getAccountById(accountId).orElseThrow();

        Assertions.assertEquals("Bangladesh", createdAccount.getCountry().getName());
        Assertions.assertTrue(createdAccount.getBalanceList().isEmpty());
    }

    @Test
    void testInsertAccountToAlreadyAccountHolder() {
        Account newAccount = new Account();
        newAccount.setCustomer(testCustomer);
        newAccount.setCountry(testCountry);

        long accountId = accountMapper.insertAccount(newAccount);
        Assertions.assertTrue(accountId > 0);
        Assertions.assertThrows(Exception.class, ()-> {accountMapper.insertAccount(newAccount);});
    }

    @Test
    void testGetInvalidAccountById() {
        Optional<Account> account = accountMapper.getAccountById(9999);
        Assertions.assertFalse(account.isPresent());
    }

    @AfterEach
    void teardown() {
        customerMapper.deleteCustomerById(testCustomer.getId());
    }
}
