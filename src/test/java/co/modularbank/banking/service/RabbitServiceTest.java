package co.modularbank.banking.service;

import co.modularbank.banking.amqp.message.BaseRabbitMessage;
import co.modularbank.banking.amqp.message.TransactionMessage;
import co.modularbank.banking.controller.model.SaveTransactionResponse;
import co.modularbank.banking.controller.model.TransactionResponse;
import co.modularbank.banking.domain.TransactionDirection;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class RabbitServiceTest {
    @Autowired RabbitService rabbitService;

    @Test
    void testSendRabbitMessage(){
        SaveTransactionResponse response = new SaveTransactionResponse(
                1,
                1,
                BigDecimal.valueOf(5.5),
                "EUR",
                TransactionDirection.IN.name(),
                "Description 1",
                BigDecimal.valueOf(20.5)
        );
        BaseRabbitMessage message = new TransactionMessage(
                "Account created with id - 1",
                response
            );
        rabbitService.sendMessage(message);
    }
}
