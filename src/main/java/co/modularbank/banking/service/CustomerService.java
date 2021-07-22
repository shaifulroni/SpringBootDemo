package co.modularbank.banking.service;

import co.modularbank.banking.controller.error.CustomerException;
import co.modularbank.banking.controller.model.CustomerRequest;
import co.modularbank.banking.controller.model.CustomerResponse;

public interface CustomerService {
    public CustomerResponse addCustomer(CustomerRequest request) throws CustomerException;
}
