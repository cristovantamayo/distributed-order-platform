package com.cristovantamayo.order.service;

import com.cristovantamayo.order.exception.ResourceNotFoundException;
import com.cristovantamayo.order.model.OrderDTO;
import com.cristovantamayo.order.model.OrderStatusDTO;
import com.cristovantamayo.order.model.OrderStatusEnum;
import com.cristovantamayo.order.repository.OrderRepository;
import com.cristovantamayo.order.repository.entities.OrderEntity;
import com.cristovantamayo.order.service.mapper.OrderMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderPersistenceService persistence;
    private final OrderRepository repository;

    @Override
    public OrderDTO save(String idempotencyKey, OrderDTO orderDTO) {

        Optional<OrderEntity> existingOrderOpt =
                persistence.findExistingOrderReadOnly(idempotencyKey);

        if (existingOrderOpt.isPresent()) {
            OrderEntity existingOrder = existingOrderOpt.get();

            if (!existingOrder.getMessageSent()) {
                persistence.sendMessageAndUpdateStatusTrue(idempotencyKey, existingOrder);
                existingOrder.setMessageSent(true);
            }

            return OrderMapper.INSTANCE.toDTO(existingOrder);
        }

        return persistence.createNewOrder(idempotencyKey, orderDTO);
    }


    @Override
    public Page<OrderDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(OrderMapper.INSTANCE::toDTO);
    }

    @Override
    public OrderDTO getOrder(UUID orderId) {
        return repository.findById(orderId)
                .map(OrderMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

    }

    @Override
    public OrderStatusDTO updateOrderStatus(OrderStatusDTO orderStatusDTO, UUID orderId) {
        OrderStatusEnum orderStatus = orderStatusDTO.getEventType();
        return Optional.of(repository.updateEventTypeById(orderStatus, orderId))
                .filter(lines -> lines > 0)
                .map(success -> OrderStatusDTO.builder().eventType(orderStatus).orderId((orderId)).build())
                .orElseThrow(() -> new EntityNotFoundException("Order not Found" + orderId));
    }
}
