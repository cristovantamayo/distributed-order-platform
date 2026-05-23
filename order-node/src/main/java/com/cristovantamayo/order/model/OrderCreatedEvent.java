package com.cristovantamayo.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {
    private String eventType;
    private UUID orderId;
    private List<Items> items;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Items {
        private String productId;
        private BigDecimal quantity;
    }
}
