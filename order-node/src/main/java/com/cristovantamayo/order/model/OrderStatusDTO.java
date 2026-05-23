package com.cristovantamayo.order.model;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@Data
public class OrderStatusDTO {
    private OrderStatusEnum eventType;

    @With
    private UUID orderId;
}
