package co.modularbank.banking.mapper;

import co.modularbank.banking.domain.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class CustomerMapperTest {
    private CustomerMapper customerMapper;

    long customerId;

    @Autowired
    private CustomerMapperTest(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Test
    void testInsertCustomer() {
        String generatedEmail = UUID.randomUUID() + "@gmail.com";
        Customer customer = new Customer(
                "Test",
                "Name",
                generatedEmail,
                "8801717001122");
        customerId = customerMapper.insertCustomer(customer);
        Assertions.assertTrue(customerId > 0);
    }

    @Test
    void testCustomerNotPresent() {
        Optional<Customer> customer = customerMapper.getCustomerById(999999);
        Assertions.assertTrue(customer.isEmpty());
    }

    @Test
    void testInsertCustomerThenGet(){
        String generatedEmail = UUID.randomUUID() + "@gmail.com";
        Customer customer = new Customer(
                "First",
                "Last",
                generatedEmail,
                "8801717000000");
        customerId = customerMapper.insertCustomer(customer);
        Assertions.assertTrue(customerId > 0);

        Customer getCustomer = customerMapper.getCustomerById(customerId).orElseThrow();
        Assertions.assertEquals("First", getCustomer.getFirstName());
        Assertions.assertEquals("Last", getCustomer.getLastName());
        Assertions.assertEquals(generatedEmail, getCustomer.getEmail());
        Assertions.assertEquals("8801717000000", getCustomer.getPhoneNumber());
    }

    @AfterEach
    void teardown() {
        if(customerId > 0L) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
