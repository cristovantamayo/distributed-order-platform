package com.cristovantamayo.order.service;

import com.cristovantamayo.order.model.OrderCreatedEvent;
import com.cristovantamayo.order.model.OrderDTO;
import com.cristovantamayo.order.repository.OrderMessageLogRepository;
import com.cristovantamayo.order.repository.OrderRepository;
import com.cristovantamayo.order.repository.entities.OrderEntity;
import com.cristovantamayo.order.repository.entities.OrderMessageAttemptEntity;
import com.cristovantamayo.order.service.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.cristovantamayo.order.service.OrderService.MESSAGE_SENT_FALSE;
import static com.cristovantamayo.order.service.OrderService.MESSAGE_SENT_TRUE;

@Component
@AllArgsConstructor
public class OrderPersistenceServiceImpl implements OrderPersistenceService {

    private final OrderRepository repository;
    private final OrderMessageLogRepository orderMessageLogRepository;
    private final OrderEventProducer publisher;

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
        orderEntity.setMessageSent(MESSAGE_SENT_FALSE);

        if (orderEntity.getItems() != null)
            orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));

        OrderEntity createdOrderEntity = repository.save(orderEntity);

        publisher.publishOrderCreated(buildEventMessage(orderEntity), buildNewOrderDTOWithID(orderDTO, orderId));

        createdOrderEntity.setMessageSent(MESSAGE_SENT_TRUE);
        repository.save(createdOrderEntity);

        return OrderMapper.INSTANCE.toDTO(createdOrderEntity);

    }

    @Override
    public void sendUpdateOrderMessageSent(OrderEntity order, boolean messageSentStatus) {
        repository.updateMessageSentStatusByOrderId(order.getId(), MESSAGE_SENT_FALSE);
        order.setMessageSent(MESSAGE_SENT_FALSE);
    }

    @Override
    public void sendUpdateOrderMessageSentByOrderId(UUID orderId, boolean messageSentStatus) {
        repository.updateMessageSentStatusByOrderId(orderId, messageSentStatus);
    }

    @Override
    @Transactional
    public void saveOrUpdateMessageLog(OrderMessageAttemptEntity orderMessageAttemptEntity) {
        orderMessageLogRepository.save(orderMessageAttemptEntity);
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
        publisher.publishOrderCreated(buildEventMessage(order), OrderMapper.INSTANCE.toDTO(order));
        repository.updateMessageSentStatusTrue(idempotencyKey);
        order.setMessageSent(MESSAGE_SENT_TRUE);
    }

    private OrderDTO buildNewOrderDTOWithID(OrderDTO orderDTO, UUID orderId) {
        return OrderDTO.builder()
                .id(orderId)
                .customerId(orderDTO.customerId())
                .items(orderDTO.items())
                .totalAmount(orderDTO.totalAmount())
                .eventType(orderDTO.eventType())
                .createdAt(orderDTO.createdAt())
                .build();

    }
}
