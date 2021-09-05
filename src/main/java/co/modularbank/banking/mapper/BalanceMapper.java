package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Balance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BalanceMapper {

    public List<Balance> getBalanceForAccountId(@Param("id") long id);

    public Optional<Balance> getAccountBalanceWithCurrency(long accountId, long currencyId);

    public long addBalanceToAccount(@Param("accountId") long accountId, Balance balance);

    public void updateAccountBalanceWithCurrency(long accountId, long currencyId, BigDecimal amount);
}
