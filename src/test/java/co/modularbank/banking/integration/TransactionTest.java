package co.modularbank.banking.integration;

import co.modularbank.banking.amqp.RabbitMessageListener;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.domain.Customer;
import co.modularbank.banking.domain.TransactionDirection;
import co.modularbank.banking.mapper.CustomerMapper;
import co.modularbank.banking.service.AccountService;
import co.modularbank.banking.service.TransactionService;
import org.hamcrest.Matchers;
import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    @MockBean
    RabbitMessageListener rabbitMessageListener;

    private long customerId;
    private long accountId;

    @BeforeEach
    void setup() {
        Customer newCustomer = new Customer("Balance", "Mapper", UUID.randomUUID() + "@gmail.com", "11223344");
        customerId = customerMapper.insertCustomer(newCustomer);

        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setCustomerId(customerId);
        accountRequest.setCountryCode("BD");
        Set<String> currencyList = new HashSet<>();
        currencyList.add("USD");
        currencyList.add("EUR");

        accountRequest.setCurrencyCodes(currencyList);

        Assertions.assertDoesNotThrow(() -> {
            accountId = accountService.createAccount(accountRequest).getAccountId();
        });
    }

    private TransactionRequest getTransactionRequest(long tempAccountId) {
        TransactionRequest request = new TransactionRequest();
        request.setAccountId(tempAccountId);
        request.setAmount(BigDecimal.valueOf(2.2));
        request.setCurrency("USD");
        request.setDirection(TransactionDirection.IN.name());
        request.setDescription("Transaction #In -> $2.2");
        return request;
    }

    @Test
    void givenTransactionRequest_whenMakeTransaction_thenCheckSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":2.2,\"currency\":\"EUR\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance", Matchers.comparesEqualTo(BigDecimal.valueOf(2.2)), BigDecimal.class))
                .andExpect(jsonPath("$.amount", Matchers.comparesEqualTo(BigDecimal.valueOf(2.2)), BigDecimal.class))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void givenMultipleTransactionRequest_whenMakeTransaction_thenCheckSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":2.2,\"currency\":\"EUR\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance", Matchers.comparesEqualTo(BigDecimal.valueOf(2.2)), BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":2.2,\"currency\":\"EUR\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance", Matchers.comparesEqualTo(BigDecimal.valueOf(4.4)), BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":2.2,\"currency\":\"EUR\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance", Matchers.comparesEqualTo(BigDecimal.valueOf(6.6)), BigDecimal.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":2.2,\"currency\":\"EUR\",\"direction\":\"OUT\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance", Matchers.comparesEqualTo(BigDecimal.valueOf(4.4)), BigDecimal.class));
    }

    @Test
    void givenInvalidAccountId_whenMakeTransaction_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + 99999 + ",\"amount\":5.0,\"currency\":\"USD\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Account missing"));
    }

    @Test
    void givenInvalidCurrencyId_whenMakeTransaction_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":5.0,\"currency\":\"ABC\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid currency"));
    }

    @Test
    void givenInActiveCurrencyId_whenMakeTransaction_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":5.0,\"currency\":\"GBP\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Currency not available for this account"));
    }

    @Test
    void givenLargeOUTAmountThenBalance_whenMakeTransaction_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":5.0,\"currency\":\"EUR\",\"direction\":\"OUT\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    void givenAccountId_whenRequestAllTransaction_thenCheckCorrect() throws Exception {
        TransactionRequest request = getTransactionRequest(accountId);

        transactionService.makeTransaction(request);
        transactionService.makeTransaction(request);
        transactionService.makeTransaction(request);

        request.setDirection(TransactionDirection.OUT.name());
        transactionService.makeTransaction(request);

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/account/" + accountId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(4)))
                .andExpect(jsonPath("$[0].direction").value("OUT"))
                .andExpect(jsonPath("$[0].amount", Matchers.comparesEqualTo(BigDecimal.valueOf(2.2)), BigDecimal.class));
    }
    @Test
    void givenInvalidAccountId_whenRequestAllTransaction_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/account/9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid account"));
    }

    @Test
    void givenNegativeAmount_whenMakeTransaction_thenCheckFailed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":" + accountId + ",\"amount\":-5.0,\"currency\":\"EUR\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid amount"));
    }

    @AfterEach
    void teardown() {
        if(customerId > 0) {
            customerMapper.deleteCustomerById(customerId);
        }
    }
}
