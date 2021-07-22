package co.modularbank.banking.integration;

import co.modularbank.banking.amqp.RabbitMessageListener;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.domain.Customer;
import co.modularbank.banking.mapper.CustomerMapper;
import co.modularbank.banking.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    AccountService accountService;

    @MockBean
    RabbitMessageListener rabbitMessageListener;

    private long customerId;

    @BeforeEach
    void setup() {
        Customer newCustomer = new Customer("FirstName", "LastName", UUID.randomUUID() + "@email.com", "8801231234");
        customerId = customerMapper.insertCustomer(newCustomer);
    }

    @Test
    void givenAccountId_whenGetAccountById_thenVerifyAccountExists() throws Exception {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCustomerId(customerId);
        accountRequest.setCountryCode("BD");
        Set<String> currencyList = new HashSet<>();
        currencyList.add("SEK");
        currencyList.add("GBP");
        accountRequest.setCurrencyCodes(currencyList);

        long accountId = accountService.createAccount(accountRequest).getAccountId();

        mockMvc.perform(MockMvcRequestBuilders.get("/account/" + accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.balance[0].amount").value(0.0))
                .andExpect(jsonPath("$.balance[0].currency").isString())
                .andExpect(jsonPath("$.balance[1].amount").value(0.0))
                .andExpect(jsonPath("$.balance[1].currency").isString());
    }

    @Test
    void givenInvalidAccountId_whenGetAccountById_thenVerifyAccountExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/account/99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("No account found"));
    }

    @Test
    void givenCustomerCountryBalance_whenInsertAccount_thenVerifyAccountCreated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + customerId +",\"countryCode\":\"BD\",\"currencyCodes\":[\"USD\",\"EUR\"]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").isNumber())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.balance[0].amount").value(0.0))
                .andExpect(jsonPath("$.balance[0].currency").isString())
                .andExpect(jsonPath("$.balance[1].amount").value(0.0))
                .andExpect(jsonPath("$.balance[1].currency").isString());
    }

    @Test
    void givenInvalidCustomer_whenInsertAccount_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + 9999 +",\"countryCode\":\"EE\",\"currencyCodes\":[\"USD\",\"EUR\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Customer not found"));
    }

    @Test
    void givenExistingAccount_whenInsertAccount_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + customerId +",\"countryCode\":\"EE\",\"currencyCodes\":[\"USD\",\"EUR\"]}"))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + customerId +",\"countryCode\":\"EE\",\"currencyCodes\":[\"USD\",\"EUR\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Account exists for this customer"));
    }

    @Test
    void givenInvalidCountry_whenInsertAccount_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + customerId +",\"countryCode\":\"XY\",\"currencyCodes\":[\"USD\",\"EUR\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Country is not supported"));
    }

    @Test
    void givenInvalidCurrency_whenInsertAccount_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":" + customerId +",\"countryCode\":\"BD\",\"currencyCodes\":[\"USD\",\"XYZ\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid or unsupported currency"));
    }

    @AfterEach
    void teardown() {
        if(customerId > 0) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
