package com.cristovantamayo.inventory.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class InventoryMessagingConfig {

    @Bean(name = "inventoryRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory inventoryRabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("inventoryTransactionManager") PlatformTransactionManager transactionManager) {

        // 🛠️ HARD-RESET THE UNDERLYING WORKER CHANNELS TO EXCLUDE CONFIRMS
        if (connectionFactory instanceof CachingConnectionFactory cachingFactory) {
            cachingFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.NONE);
            cachingFactory.setPublisherReturns(false);
        }

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setTransactionManager(transactionManager);
        factory.setPhase(Integer.MAX_VALUE);
        factory.setConcurrentConsumers(1);

        return factory;
    }
}
