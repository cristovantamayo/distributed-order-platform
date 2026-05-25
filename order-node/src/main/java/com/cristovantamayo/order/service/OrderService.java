package com.cristovantamayo.order.service;

import com.cristovantamayo.order.model.OrderDTO;
import com.cristovantamayo.order.model.OrderStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    
    public static final boolean MESSAGE_SENT_FALSE = false;
    public static final boolean MESSAGE_SENT_TRUE = true;

    OrderDTO save(String idempotencyKey, OrderDTO orderDAO);

    Page<OrderDTO> getAll(Pageable pageable);

    OrderDTO getOrder(UUID orderId);

    OrderStatusDTO updateOrderStatus(OrderStatusDTO orderStatusDTO, UUID orderId);
}
