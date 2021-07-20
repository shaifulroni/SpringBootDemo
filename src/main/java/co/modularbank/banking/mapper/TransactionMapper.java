package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TransactionMapper {

    List<Transaction> getTransactionsByAccountId(@Param("id") long accountId);

    long insertTransaction(Transaction transaction);
}