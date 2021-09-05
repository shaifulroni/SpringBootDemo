package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.CustomerException;
import co.modularbank.banking.controller.model.CustomerResponse;
import co.modularbank.banking.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {
    private MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    @Autowired
    public CustomerControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void givenExistingEmail_whenAddCustomer_thenCheckFailed() throws Exception {
        Mockito.when(customerService.addCustomer(any()))
                .thenThrow(new CustomerException("Customer exists with this email"));

        mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"First\",\"lastName\":\"Last\",\"email\":\"email@gmail.com\",\"phoneNumber\":\"123456789\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Customer exists with this email"));
    }

    @Test
    void givenCustomerInfo_whenAddCustomer_thenCheckAdded() throws Exception {
        CustomerResponse customerResp = new CustomerResponse(1, "First Last", "email@gmail.com", "123456789");
        Mockito.when(customerService.addCustomer(any()))
                .thenReturn(customerResp);

        mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"First\",\"lastName\":\"Last\",\"email\":\"email@gmail.com\",\"phoneNumber\":\"123456789\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("First Last"))
                .andExpect(jsonPath("$.customerId").value(1));
    }
}
