package co.modularbank.banking.service.impl;

import co.modularbank.banking.amqp.message.BaseRabbitMessage;
import co.modularbank.banking.service.RabbitService;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitServiceImpl implements RabbitService {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    TopicExchange rabbitExchange;

    @Override
    public void sendMessage(BaseRabbitMessage message) {
        rabbitTemplate.convertAndSend(rabbitExchange.getName(), "accounting.message." + message.getType() , message);
    }
}
