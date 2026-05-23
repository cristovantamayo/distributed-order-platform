package com.cristovantamayo.order.service;

import com.cristovantamayo.order.model.OrderDTO;
import com.cristovantamayo.order.repository.entities.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderPersistenceService {
    Optional<OrderEntity> findExistingOrderReadOnly(String idempotencyKey);
    OrderDTO createNewOrder(String idempotencyKey, OrderDTO orderDTO);
    void sendMessageAndUpdateStatusTrue(String idempotencyKey, OrderEntity order);
}
