package com.cristovantamayo.order.repository.entities;

import com.cristovantamayo.order.model.OrderPublishStatusEnum;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Builder
@Entity
@Table(name = "ORDER_MESSAGE_ATTEMPT", schema = "APP_ORDER")
@AllArgsConstructor
public class OrderMessageAttemptEntity {

    // Getters e Setters
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "ID", length = 36, nullable = false)
    private UUID id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "ORDER_ID", length = 36, nullable = false)
    private UUID orderId;

    @Column(name = "CORRELATION_ID", length = 100, nullable = false)
    private String correlationId;

    @Column(name = "EVENT_TYPE", length = 100, nullable = false)
    private String eventType;

    @Column(name = "ROUTING_KEY", length = 150, nullable = false)
    private String routingKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private OrderPublishStatusEnum status;

    @Column(name = "ERROR_MESSAGE", length = 1000)
    private String errorMessage;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    // Preenche automaticamente a data de criação antes de salvar no banco
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Preenche automaticamente a data de atualização antes de alterar no banco
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Construtor padrão obrigatório pelo JPA
    public OrderMessageAttemptEntity() {
    }

    // Equals e HashCode baseados no ID (boa prática para entidades JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderMessageAttemptEntity that = (OrderMessageAttemptEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
