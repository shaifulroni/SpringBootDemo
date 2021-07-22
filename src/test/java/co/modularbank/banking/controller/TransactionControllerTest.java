package co.modularbank.banking.controller;

import co.modularbank.banking.controller.error.TransactionException;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.model.TransactionRequest;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.domain.TransactionDirection;
import co.modularbank.banking.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    TransactionService transactionService;

    @Test
    void givenTransactionRequest_whenMakeTransaction_thenCheckSuccess() throws Exception {
        SaveTransactionResponse saveResponse = new SaveTransactionResponse(1, 1, 55.0, "EUR", TransactionDirection.IN.name(), "Transaction 1", 55.0);

        Mockito.when(transactionService.makeTransaction(ArgumentMatchers.any(TransactionRequest.class)))
                .thenReturn(saveResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":1,\"amount\":55.0,\"currency\":\"EUR\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.amount").value(55.0))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.direction").value("IN"))
                .andExpect(jsonPath("$.balance").value(55.0));
    }

    @Test
    void givenAccountId_whenGetTransactionList_thenCheckCorrect() throws Exception {
        List<TransactionResponse> transactionList = new ArrayList<>();
        transactionList.add(new TransactionResponse(1, 1, 55.0, "EUR", TransactionDirection.IN.name(), "Transaction 1"));

        Mockito.when(transactionService.getTransactionsForAccount(1))
                .thenReturn(transactionList);

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/account/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[0].amount").value(55.0))
                .andExpect(jsonPath("$[0].currency").value("EUR"))
                .andExpect(jsonPath("$[0].direction").value("IN"));
    }

    @Test
    void givenInvalidAccountId_whenMakeTransaction_thenCheckFailed() throws Exception {
        Mockito.when(transactionService.makeTransaction(ArgumentMatchers.any(TransactionRequest.class)))
                .thenThrow(new TransactionException("Invalid account"));

        mockMvc.perform(MockMvcRequestBuilders.post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":9999,\"amount\":55.0,\"currency\":\"EUR\",\"direction\":\"IN\",\"description\":\"Transaction 1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid account"));
    }

    @Test
    void givenInvalidAccountId_whenGetTransactionList_thenCheckFailed() throws Exception {
        Mockito.when(transactionService.getTransactionsForAccount(9999))
                .thenThrow(new TransactionException("Invalid account"));

        mockMvc.perform(MockMvcRequestBuilders.get("/transaction/account/9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid account"));
    }
}
