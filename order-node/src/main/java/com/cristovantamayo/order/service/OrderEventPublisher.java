package com.cristovantamayo.order.service;

import com.cristovantamayo.config.MessagingTopologyConfig;
import com.cristovantamayo.order.model.OrderCreatedEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Validator validator;

    @Value("${app.rabbitmq.order-exchange:orders.events.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.order-routing-key:orders.created}")
    private String routingKey;

    public void publishOrderCreated(OrderCreatedEvent event) {

        validate(event);

        rabbitTemplate.convertAndSend(
                exchangeName,
                "Abacate", //routingKey,
                event
        );
    }



    private void validate(OrderCreatedEvent event) {
        Set<ConstraintViolation<OrderCreatedEvent>> violations = validator.validate(event);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
