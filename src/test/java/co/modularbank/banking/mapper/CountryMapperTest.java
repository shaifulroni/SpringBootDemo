package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class CountryMapperTest {
    private CountryMapper countryMapper;

    @Autowired
    public CountryMapperTest(CountryMapper countryMapper) {
        this.countryMapper = countryMapper;
    }

    @Test
    void testCountryExists() {
        Optional<Country> country = countryMapper.getCountryByCodeName("BD");
        Assertions.assertTrue(country.isPresent());
        Assertions.assertEquals("Bangladesh", country.get().getName());
        Assertions.assertEquals("BD", country.get().getCode());
    }

    @Test
    void testCountryNotExists() {
        Optional<Country> country = countryMapper.getCountryByCodeName("KR");
        Assertions.assertTrue(country.isEmpty());
    }
}
