package com.cristovantamayo.order.service;

import com.cristovantamayo.order.model.OrderDTO;
import com.cristovantamayo.order.model.OrderStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    OrderDTO save(String idempotencyKey, OrderDTO orderDAO);

    Page<OrderDTO> getAll(Pageable pageable);

    OrderDTO getOrder(UUID orderId);

    OrderStatusDTO updateOrderStatus(OrderStatusDTO orderStatusDTO, UUID orderId);
}
