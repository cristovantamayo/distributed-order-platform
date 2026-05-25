package com.cristovantamayo.order.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import org.springframework.amqp.core.ReturnedMessage;

import java.util.UUID;

@Builder
public record OrderPublishEvent(UUID messageId,
                                String correlationId,
                                OrderDTO orderDTO,
                                String routingKey,
                                String messageError,
                                OrderPublishStatusEnum status,
                                boolean applyRollback) {}
