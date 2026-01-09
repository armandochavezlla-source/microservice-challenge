package com.challenge.order.repo;

import com.challenge.order.domain.OrderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<OrderEntity, Long> {}
