package com.helloword.order.repository.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;

    public OrderItemEntity() {}
}
