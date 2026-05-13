package com.cristovantamayo.order.controller;

import com.cristovantamayo.order.dao.OrderDTO;
import com.cristovantamayo.order.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/health")
    public String hello() {
        return "Order Resource is health";
    }

    @PostMapping
    public ResponseEntity<OrderDTO> create(@RequestBody OrderDTO orderDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.save(orderDTO));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 20, sort = "id",
                    direction = Sort.Direction.DESC, page = 0) Pageable pageable){
        return ResponseEntity.ok(orderService.getAll(pageable));
    }

    @GetMapping("/{orderId}")
    public OrderDTO getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }
}
