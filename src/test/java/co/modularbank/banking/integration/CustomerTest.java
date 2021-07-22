package co.modularbank.banking.integration;

import co.modularbank.banking.mapper.CustomerMapper;
import co.modularbank.banking.service.CustomerService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CustomerService accountService;

    private Integer customerId;

    @Test
    void givenNewCustomer_whenAddCustomer_thenCheckAdded() throws Exception {
        String genEmail = UUID.randomUUID() + "@gmail.com";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"First\",\"lastName\":\"Last\",\"email\":\"" + genEmail + "\",\"phoneNumber\":\"123456789\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("First Last"))
                .andExpect(jsonPath("$.email").value(genEmail))
                .andExpect(jsonPath("$.customerId").isNumber())
                .andReturn();

        customerId = JsonPath.read(result.getResponse().getContentAsString(), "$.customerId");
    }

    @Test
    void givenExistingEmail_whenAddCustomer_thenCheckAdded() throws Exception {
        String genEmail = UUID.randomUUID() + "@gmail.com";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"First\",\"lastName\":\"Last\",\"email\":\"" + genEmail + "\",\"phoneNumber\":\"123456789\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("First Last"))
                .andExpect(jsonPath("$.email").value(genEmail))
                .andExpect(jsonPath("$.customerId").isNumber())
                .andReturn();

        customerId = JsonPath.read(result.getResponse().getContentAsString(), "$.customerId");

        mockMvc.perform(MockMvcRequestBuilders.post("/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"First\",\"lastName\":\"Last\",\"email\":\"" + genEmail + "\",\"phoneNumber\":\"123456789\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @AfterEach
    void teardown() {
        if(customerId > 0) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
