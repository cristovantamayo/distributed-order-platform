package com.cristovantamayo.inventory.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
    @RabbitListener(
            queues = "${spring.rabbitmq.order-queue:inventory.order-created.queue}",
            containerFactory = "inventoryRabbitListenerContainerFactory"
    )
    public void consume(String message){
        System.out.println("Inventory Recepts: " + message);
    }
}
