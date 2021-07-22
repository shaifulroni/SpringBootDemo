package co.modularbank.banking.service.impl;

import co.modularbank.banking.controller.error.CustomerException;
import co.modularbank.banking.controller.model.CustomerRequest;
import co.modularbank.banking.controller.model.CustomerResponse;
import co.modularbank.banking.domain.Customer;
import co.modularbank.banking.mapper.CustomerMapper;
import co.modularbank.banking.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    CustomerMapper customerMapper;

    @Override
    public CustomerResponse addCustomer(CustomerRequest request) throws CustomerException {
        Optional<Customer> customer = customerMapper.getCustomerByEmail(request.getEmail());
        if(customer.isPresent()) throw new CustomerException("Customer exists with this email");

        Customer newCustomer = new Customer(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPhoneNumber()
                );
        long newCustomerId = customerMapper.insertCustomer(newCustomer);
        return new CustomerResponse(
                newCustomerId,
                newCustomer.getFirstName() + " " + newCustomer.getLastName(),
                newCustomer.getEmail(),
                newCustomer.getPhoneNumber()
        );
    }
}
