package com.cristovantamayo.order.repository.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "VARCHAR(36)")
    private OrderEntity order;

    @Column(name = "product_id")
    private String productId;

    private BigDecimal quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    public OrderItemEntity() {}
}

