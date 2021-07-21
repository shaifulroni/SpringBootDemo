package co.modularbank.banking.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    public static final String ACCOUNTING_EXCHANGE = "accounting-exchange";
    public static final String ACCOUNTING_QUEUE = "accounting-queue";
    public static final String ACCOUNTING_TOPIC = "accounting.message.#";

    @Bean
    public Queue queue() {
        return new Queue(ACCOUNTING_QUEUE, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(ACCOUNTING_EXCHANGE);//, true, false);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ACCOUNTING_TOPIC);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
