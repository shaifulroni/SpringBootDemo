package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.*;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class BalanceMapperTest {
    private BalanceMapper balanceMapper;
    private CurrencyMapper currencyMapper;
    private CustomerMapper customerMapper;
    private AccountMapper accountMapper;
    private CountryMapper countryMapper;

    private Customer testCustomer;
    private Account testAccount;
    private Country testCountry;

    @Autowired
    public BalanceMapperTest(BalanceMapper balanceMapper,
                             CurrencyMapper currencyMapper,
                             CustomerMapper customerMapper,
                             AccountMapper accountMapper,
                             CountryMapper countryMapper) {
        this.balanceMapper = balanceMapper;
        this.currencyMapper = currencyMapper;
        this.customerMapper = customerMapper;
        this.accountMapper = accountMapper;
        this.countryMapper = countryMapper;
    }

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
    void testAddBalanceWithInvalidAccount() {
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(2.2));
        balance.setCurrency(currencyMapper.getCurrencyByShortName("USD").orElseThrow());
        Assertions.assertThrows(Exception.class, () -> balanceMapper.addBalanceToAccount(9999, balance));
    }

    @Test
    void testAddBalanceWithCurrencyAndGet() {
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(1.1));

        balance.setCurrency(currencyMapper.getCurrencyByShortName("EUR").orElseThrow());
        long balanceId = balanceMapper.addBalanceToAccount(testAccount.getId(), balance);
        Assertions.assertTrue(balanceId > 0);

        balance.setAmount(BigDecimal.valueOf(2.2));
        balance.setCurrency(currencyMapper.getCurrencyByShortName("USD").orElseThrow());
        balanceId = balanceMapper.addBalanceToAccount(testAccount.getId(), balance);
        Assertions.assertTrue(balanceId > 0);

        balance.setAmount(BigDecimal.valueOf(3.3));
        balance.setCurrency(currencyMapper.getCurrencyByShortName("GBP").orElseThrow());
        balanceId = balanceMapper.addBalanceToAccount(testAccount.getId(), balance);
        Assertions.assertTrue(balanceId > 0);

        balance.setAmount(BigDecimal.valueOf(4.4));
        balance.setCurrency(currencyMapper.getCurrencyByShortName("SEK").orElseThrow());
        balanceId = balanceMapper.addBalanceToAccount(testAccount.getId(), balance);
        Assertions.assertTrue(balanceId > 0);

        List<Balance> balanceList = balanceMapper.getBalanceForAccountId(testAccount.getId());
        Assertions.assertEquals(4, balanceList.size());
    }

    @Test
    void testGetAccountBalanceWithCurrency() {
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(2.2));

        Currency euro = currencyMapper.getCurrencyByShortName("EUR").orElseThrow();
        balance.setCurrency(euro);
        long balanceId = balanceMapper.addBalanceToAccount(testAccount.getId(), balance);
        Assertions.assertTrue(balanceId > 0);

        Balance acBalance = balanceMapper.getAccountBalanceWithCurrency(testAccount.getId(), euro.getId()).orElseThrow();
        Assertions.assertEquals("Euro", acBalance.getCurrency().getName());
        MatcherAssert.assertThat(acBalance.getAmount(), Matchers.comparesEqualTo(BigDecimal.valueOf(2.20)));
    }

    @Test
    void testUpdateAccountBalanceWithCurrency() {
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(2.2));

        Currency euro = currencyMapper.getCurrencyByShortName("EUR").orElseThrow();
        balance.setCurrency(euro);
        long balanceId = balanceMapper.addBalanceToAccount(testAccount.getId(), balance);
        Assertions.assertTrue(balanceId > 0);

        balanceMapper.updateAccountBalanceWithCurrency(testAccount.getId(), euro.getId(), BigDecimal.valueOf(9.9));
        Balance updatedBalance = balanceMapper.getAccountBalanceWithCurrency(testAccount.getId(), euro.getId()).orElseThrow();
        MatcherAssert.assertThat(updatedBalance.getAmount(), Matchers.comparesEqualTo(BigDecimal.valueOf(9.9)));

        balanceMapper.updateAccountBalanceWithCurrency(testAccount.getId(), euro.getId(), BigDecimal.ZERO);
        updatedBalance = balanceMapper.getAccountBalanceWithCurrency(testAccount.getId(), euro.getId()).orElseThrow();
        MatcherAssert.assertThat(updatedBalance.getAmount(), Matchers.comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    void testUpdateAccountBalanceWithInvalidAmount() {
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(2.2));

        Currency euro = currencyMapper.getCurrencyByShortName("EUR").orElseThrow();
        balance.setCurrency(euro);
        long balanceId = balanceMapper.addBalanceToAccount(testAccount.getId(), balance);
        Assertions.assertTrue(balanceId > 0);

        Assertions.assertThrows(Exception.class, () -> balanceMapper.updateAccountBalanceWithCurrency(testAccount.getId(), euro.getId(), BigDecimal.valueOf(-9.9)));
    }

    @AfterEach
    void teardown() {
        customerMapper.deleteCustomerById(testCustomer.getId());
    }
}
