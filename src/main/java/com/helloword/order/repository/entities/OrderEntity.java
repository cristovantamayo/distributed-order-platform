package com.helloword.order.repository.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Native UUID support in Hibernate 6
    private UUID id;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private String customerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> items = new ArrayList<>();

    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;

    // Standard no-args constructor required by JPA
    public OrderEntity() {}

    // Getters and Setters...
}

