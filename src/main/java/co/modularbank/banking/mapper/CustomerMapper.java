package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CustomerMapper {

    public List<Customer> getAllCustomers();

    public Optional<Customer> getCustomerById(@Param("id") long id);

    public long insertCustomer(Customer customer);
}
