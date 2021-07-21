package co.modularbank.banking.amqp;

import co.modularbank.banking.amqp.message.AccountMessage;
import co.modularbank.banking.amqp.message.TransactionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMessageListener {
    private final Logger logger = LoggerFactory.getLogger(RabbitMessageListener.class);

    @RabbitListener(queues = RabbitConfiguration.ACCOUNTING_QUEUE)
    public void receiveMessage(AccountMessage message) {
        logger.info("Received account info <" + message + ">");
        System.out.println(message);
    }

    @RabbitListener(queues = RabbitConfiguration.ACCOUNTING_QUEUE)
    public void receiveMessage(TransactionMessage message) {
        logger.info("Received transaction info <" + message + ">");
        System.out.println(message);
    }
}
