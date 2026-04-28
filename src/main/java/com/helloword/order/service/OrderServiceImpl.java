package com.helloword.order.service;

import com.helloword.order.dao.OrderDAO;
import com.helloword.order.repository.OrderRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository repository;

    @Override
    public void save(OrderDAO orderDAO) {
        repository.save(orderDAO);
    }
}
