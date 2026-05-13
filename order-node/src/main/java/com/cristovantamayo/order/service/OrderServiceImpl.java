package com.cristovantamayo.order.service;

import com.cristovantamayo.order.dao.OrderDTO;
import com.cristovantamayo.order.exception.ResourceNotFoundException;
import com.cristovantamayo.order.repository.OrderRepository;
import com.cristovantamayo.order.repository.entities.OrderEntity;
import com.cristovantamayo.order.service.mapper.OrderMapper;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository repository;

    @Override
    public OrderDTO save(OrderDTO orderDAO) {
        return OrderMapper.INSTANCE.toDTO(
                repository.save(OrderMapper.INSTANCE.toEntity(orderDAO)));
    }

    @Override
    public Page<OrderDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(OrderMapper.INSTANCE::toDTO);
    }

    @Override
    public OrderDTO getOrder(Long orderId) {
        return repository.findById(orderId)
                .map(OrderMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

    }
}
