package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.CustomerException;
import co.modularbank.banking.controller.model.CustomerRequest;
import co.modularbank.banking.controller.model.CustomerResponse;
import co.modularbank.banking.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    ResponseEntity<CustomerResponse> addCustomer(@Valid @RequestBody CustomerRequest request) throws CustomerException {
        return new ResponseEntity<>(customerService.addCustomer(request), HttpStatus.CREATED);
    }
}
