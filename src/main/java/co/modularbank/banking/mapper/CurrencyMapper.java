package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Currency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface CurrencyMapper {
    public Optional<Currency> getCurrencyById(@Param("id") int id);
    public Optional<Currency> getCurrencyByShortName(String shortName);
}
