package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class CurrencyMapperTest {
    private CurrencyMapper currencyMapper;

    @Autowired
    public CurrencyMapperTest(CurrencyMapper currencyMapper) {
        this.currencyMapper = currencyMapper;
    }

    @Test
    public void testGetCurrencyById(){
        Currency currency = currencyMapper.getCurrencyById(1).orElseThrow();

        Assertions.assertEquals("EUR", currency.getShortName());
        Assertions.assertEquals("Euro", currency.getName());
    }

    @Test
    public void testGetCurrencyByCode() {
        Currency currency = currencyMapper.getCurrencyByShortName("USD").orElseThrow();
        Assertions.assertEquals("$", currency.getSymbol());
    }

    @Test
    public void testCurrencyNotFound() {
        Optional<Currency> currency = currencyMapper.getCurrencyByShortName("BDT");
        Assertions.assertTrue(currency.isEmpty());
    }
}
