package com.helloword.order.dao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDAO(
        @NotBlank String uuid,
        @NotBlank String customerId,
        @NotEmpty @Valid List<Item> items,
        @Positive BigDecimal totalAmount,
        OrderStatusEnum status,
        LocalDateTime createdAt
) {

    public record Item(
            @NotBlank String productId,
            @Positive BigDecimal quantity,
            @Positive BigDecimal unitPrice
    ) {}

    public OrderDAO {
        if (status == null)
            status = OrderStatusEnum.RECEIVED;

        if (createdAt == null)
            createdAt = LocalDateTime.now();

        if(items == null)
            items = List.of();
    }
}
