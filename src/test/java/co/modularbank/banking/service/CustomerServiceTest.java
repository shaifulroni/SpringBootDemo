package co.modularbank.banking.service;

import co.modularbank.banking.controller.error.AccountException;
import co.modularbank.banking.controller.error.CustomerException;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.controller.model.CustomerRequest;
import co.modularbank.banking.controller.model.CustomerResponse;
import co.modularbank.banking.domain.Customer;
import co.modularbank.banking.mapper.CustomerMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
public class CustomerServiceTest {
    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CustomerService customerService;

    private long customerId;

    @Test
    void givenCustomerInfo_WhenAddCustomer_CheckAdded() throws Exception {
        String genEmail = UUID.randomUUID() + "@gmail.com";

        CustomerRequest request = new CustomerRequest();
        request.setFirstName("First");
        request.setLastName("Last");
        request.setEmail(genEmail);
        request.setPhoneNumber("123456789");

        CustomerResponse response = customerService.addCustomer(request);
        Assertions.assertTrue(response.getCustomerId() > 0);
        customerId = response.getCustomerId();
        Assertions.assertEquals(genEmail, response.getEmail());
        Assertions.assertEquals("First Last", response.getName());
    }

    @Test
    void givenExistingCustomer_WhenAddCustomer_CheckFailed() throws Exception {
        String genEmail = UUID.randomUUID() + "@gmail.com";

        CustomerRequest request = new CustomerRequest();
        request.setFirstName("First");
        request.setLastName("Last");
        request.setEmail(genEmail);
        request.setPhoneNumber("123456789");

        CustomerResponse response = customerService.addCustomer(request);
        Assertions.assertTrue(response.getCustomerId() > 0);
        customerId = response.getCustomerId();

        Assertions.assertThrows(CustomerException.class, ()-> {
            customerService.addCustomer(request);
        });
    }

    @AfterEach
    void teardown() {
        if(customerId > 0) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
