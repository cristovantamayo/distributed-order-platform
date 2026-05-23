package com.cristovantamayo.order.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitProperties {

    private QueueProperties queue = new QueueProperties();
    private ExchangeProperties exchange = new ExchangeProperties();
    private RoutingProperties routing = new RoutingProperties();

    // Getters and Setters for all fields

    @Getter
    @Setter
    public static class QueueProperties {
        private String name;
    }
    @Getter
    @Setter
    public static class ExchangeProperties {
        private String name;
    }
    @Getter
    @Setter
    public static class RoutingProperties {
        private String key;
    }
}
