package co.modularbank.banking.service;

import co.modularbank.banking.amqp.message.BaseRabbitMessage;

public interface RabbitService {
    public void sendMessage(BaseRabbitMessage message);
}
