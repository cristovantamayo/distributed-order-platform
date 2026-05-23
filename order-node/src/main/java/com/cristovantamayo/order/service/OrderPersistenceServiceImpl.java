package com.cristovantamayo.order.service;

import com.cristovantamayo.order.model.OrderCreatedEvent;
import com.cristovantamayo.order.model.OrderDTO;
import com.cristovantamayo.order.repository.OrderRepository;
import com.cristovantamayo.order.repository.entities.OrderEntity;
import com.cristovantamayo.order.service.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OrderPersistenceServiceImpl implements OrderPersistenceService {

    private final OrderRepository repository;
    private final OrderEventPublisher publisher;

    @Transactional(readOnly = true)
    public Optional<OrderEntity> findExistingOrderReadOnly(String idempotencyKey) {
        return repository.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional
    public OrderDTO createNewOrder(String idempotencyKey, OrderDTO orderDTO) {
        OrderEntity orderEntity = OrderMapper.INSTANCE.toEntity(orderDTO);
        orderEntity.setIdempotencyKey(idempotencyKey);

        UUID orderId = UUID.randomUUID();
        orderEntity.setId(orderId);
        orderEntity.setMessageSent(false);

        if (orderEntity.getItems() != null)
            orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));

        OrderEntity createdOrderEntity = repository.save(orderEntity);

        publisher.publishOrderCreated(buildEventMessage(orderEntity));

        createdOrderEntity.setMessageSent(true);
        repository.save(createdOrderEntity);

        return OrderMapper.INSTANCE.toDTO(createdOrderEntity);

    }

    private static OrderCreatedEvent buildEventMessage(OrderEntity createdOrderEntity) {
        return OrderCreatedEvent.builder()
                .orderId(createdOrderEntity.getId())
                .eventType(createdOrderEntity.getOrderStatus().getEventType())
                .items(OrderMapper.INSTANCE.toEventItems(createdOrderEntity.getItems()))
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Forces an immediate, un-rollbackable commit
    public void sendMessageAndUpdateStatusTrue(String idempotencyKey, OrderEntity order) {
        publisher.publishOrderCreated(buildEventMessage(order));
        repository.updateMessageSentStatus(idempotencyKey);
        order.setMessageSent(true);
    }
}
