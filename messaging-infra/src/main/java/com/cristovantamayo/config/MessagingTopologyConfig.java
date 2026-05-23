package com.cristovantamayo.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingTopologyConfig {

    @Getter
    @Value("${app.rabbitmq.order-exchange:orders.events.exchange}")
    private String exchangeName;
    @Value("${app.rabbitmq.order-queue:inventory.order-created.queue}")
    private String queueName;
    @Value("${app.rabbitmq.order-routing-key:orders.created}")
    private String routingKey;

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public Binding binding(Queue inventoryQueue, TopicExchange ordersExchange) {
        return BindingBuilder
                .bind(inventoryQueue)
                .to(ordersExchange)
                .with(routingKey);
    }
}
