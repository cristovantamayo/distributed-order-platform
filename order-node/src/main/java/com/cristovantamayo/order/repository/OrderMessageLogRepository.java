package com.cristovantamayo.order.repository;

import com.cristovantamayo.order.repository.entities.OrderMessageAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderMessageLogRepository extends JpaRepository<OrderMessageAttemptEntity, UUID> {
}
