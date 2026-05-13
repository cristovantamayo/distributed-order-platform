package com.cristovantamayo.order.service;

import com.cristovantamayo.order.dao.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDTO save(OrderDTO orderDAO);

    Page<OrderDTO> getAll(Pageable pageable);

    OrderDTO getOrder(Long orderId);
}
