package co.modularbank.banking.integration;

import co.modularbank.banking.amqp.RabbitMessageListener;
import co.modularbank.banking.domain.Customer;
import co.modularbank.banking.mapper.CustomerMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountTransactionTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomerMapper customerMapper;

    @MockBean
    RabbitMessageListener listener;

    private long customerId;

    @BeforeEach
    void setup() {
        Customer newCustomer = new Customer("FirstName", "LastName", UUID.randomUUID() + "@email.com", "8801231234");
        customerId = customerMapper.insertCustomer(newCustomer);
    }

    @Test
    void testAccountAndTransaction() throws Exception {
        // Create account
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + customerId +",\"countryCode\":\"BD\",\"currencyCodes\":[\"USD\"]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").isNumber())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.balance[0].amount").value(0.0))
                .andExpect(jsonPath("$.balance[0].currency").value("USD"))
                .andReturn();

        Integer accountId = JsonPath.read(result.getResponse().getContentAsString(), "$.accountId");

        // Make deposit
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":2.2,\"currency\":\"USD\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(IsCloseTo.closeTo(2.2, 0.0001)));

        // Check balance
        mockMvc.perform(MockMvcRequestBuilders.get("/account/" + accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.balance[0].amount").value(IsCloseTo.closeTo(2.2,0.0001)))
                .andExpect(jsonPath("$.balance[0].currency").value("USD"));

        // Make another deposit
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":5.8,\"currency\":\"USD\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(IsCloseTo.closeTo(8.0, 0.0001)));

        // Check balance
        mockMvc.perform(MockMvcRequestBuilders.get("/account/" + accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.balance[0].amount").value(IsCloseTo.closeTo(8.0,0.0001)))
                .andExpect(jsonPath("$.balance[0].currency").value("USD"));

        // Try to withdraw more than balance
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":9.0,\"currency\":\"USD\",\"direction\":\"OUT\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds"));

        // Check balance
        mockMvc.perform(MockMvcRequestBuilders.get("/account/" + accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.balance[0].amount").value(IsCloseTo.closeTo(8.0,0.0001)))
                .andExpect(jsonPath("$.balance[0].currency").value("USD"));

        // Try to withdraw a valid amount
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":4.0,\"currency\":\"USD\",\"direction\":\"OUT\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance").value(IsCloseTo.closeTo(4.0, 0.0001)));

        // Check balance
        mockMvc.perform(MockMvcRequestBuilders.get("/account/" + accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.balance[0].amount").value(IsCloseTo.closeTo(4.0,0.0001)))
                .andExpect(jsonPath("$.balance[0].currency").value("USD"));

    }

    @AfterEach
    void teardown() {
        if(customerId > 0L) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
