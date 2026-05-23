package com.cristovantamayo.order.repository;

import com.cristovantamayo.order.model.OrderStatusDTO;
import com.cristovantamayo.order.model.OrderStatusEnum;
import com.cristovantamayo.order.repository.entities.OrderEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @EntityGraph(attributePaths = {"items"})
    Optional<OrderEntity> findByIdempotencyKey(String idempotencyKey);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE OrderEntity o SET o.messageSent = true WHERE o.idempotencyKey = :idempotencyKey")
    void updateMessageSentStatus(@Param("idempotencyKey") String idempotencyKey);

    @Modifying
    @Transactional
    @Query("UPDATE OrderEntity o SET o.orderStatus = :eventType WHERE o.id = :id")
    int updateEventTypeById(@Param("eventType") OrderStatusEnum eventType, @Param("id") UUID orderId);
}
