package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Country;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CountryMapper {

    public Optional<Country> getCountryByCodeName(@Param("countryCode") String countryCode);

    public List<Country> getAllCountries();
}
