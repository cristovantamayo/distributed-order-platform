package com.cristovantamayo.order;

import com.cristovantamayo.order.config.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class OrderConfig {
}
