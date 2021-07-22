package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.AccountException;
import co.modularbank.banking.controller.model.AccountRequest;
import co.modularbank.banking.controller.model.AccountResponse;
import co.modularbank.banking.controller.model.BalanceResponse;
import co.modularbank.banking.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @Test
    void givenInvalidAccount_whenGetAccountById_thenCheckFailed() throws Exception {
        Mockito.when(accountService.getAccountById(9999))
                .thenThrow(new AccountException("No account found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/account/9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No account found"));
    }

    @Test
    void givenValidAccount_whenGetAccountById_thenCheckCorrect() throws Exception {
        List<BalanceResponse> balanceList = new ArrayList<>();
        balanceList.add(new BalanceResponse(5.0, "USD"));

        Mockito.when(accountService.getAccountById(1))
                .thenReturn(new AccountResponse(1, 1, balanceList));

        mockMvc.perform(MockMvcRequestBuilders.get("/account/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.balance[0].amount").value(5.0))
                .andExpect(jsonPath("$.balance[0].currency").value("USD"));
    }

    @Test
    void givenCustomerIdCountryCurrency_whenCreateAccount_thenCheckCreated() throws Exception {
        List<BalanceResponse> balanceList = new ArrayList<>();
        balanceList.add(new BalanceResponse(5.0, "USD"));
        balanceList.add(new BalanceResponse(55, "EUR"));

        Mockito.when(accountService.createAccount(any(AccountRequest.class)))
                .thenReturn(new AccountResponse(1, 1, balanceList));

        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":1,\"countryCode\":\"BD\",\"currencyCodes\":[\"USD\",\"EUR\"]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.balance[0].amount").value(5.0))
                .andExpect(jsonPath("$.balance[0].currency").value("USD"))
                .andExpect(jsonPath("$.balance[1].amount").value(55))
                .andExpect(jsonPath("$.balance[1].currency").value("EUR"));
    }

    @Test
    void givenInvalidCustomerId_whenCreateAccount_thenCheckFailed() throws Exception {
        Mockito.when(accountService.createAccount(any(AccountRequest.class)))
                .thenThrow(new AccountException("Invalid account"));

        mockMvc.perform(MockMvcRequestBuilders.post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":1,\"countryCode\":\"BD\",\"currencyCodes\":[\"USD\",\"EUR\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid account"));
    }
}
