package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AccountMapper {

    public Optional<Account> getAccountById(@Param("id") long id);

    public Optional<Account> getAccountByCustomerId(@Param("id") long id);

    public long insertAccount(Account account);
}