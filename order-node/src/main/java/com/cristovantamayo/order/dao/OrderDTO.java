package com.cristovantamayo.order.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        String eventId,
        @NotBlank String customerId,
        @NotEmpty @Valid List<Item> items,
        @Positive BigDecimal totalAmount,
        OrderStatusEnum eventType,
        LocalDateTime createdAt
) {

    public record Item(
            @NotBlank String productId,
            @Positive BigDecimal quantity,
            @Positive BigDecimal unitPrice
    ) {}

    public OrderDTO {
        if (eventType == null)
            eventType = OrderStatusEnum.RECEIVED;

        if (createdAt == null)
            createdAt = LocalDateTime.now();

        if(items == null)
            items = List.of();
    }

    public List<Item> getItems() {
        return items;
    }
}
