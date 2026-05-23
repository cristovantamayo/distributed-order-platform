package com.cristovantamayo.order.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    RECEIVED("OrderCreated"),
    AWAITING_INVENTORY("OrderAwaitingInventory"),
    CONFIRMED("OrderConfirmed"),
    CANCELLED("OrderCanceled"),
    AWAITING_PAYMENT("OrderAwaitingPayment");

    @JsonValue
    private final String eventType;

    @JsonCreator
    public static OrderStatusEnum fromEventType(String eventType) {
        return Arrays.stream(OrderStatusEnum.values())
                .filter(status -> status.getEventType().equals(eventType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown event type: " + eventType));
    }
}
