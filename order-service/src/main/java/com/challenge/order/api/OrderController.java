package com.challenge.order.api;

import com.challenge.order.api.dto.CreateOrderRequest;
import com.challenge.order.api.dto.OrderResponse;
import com.challenge.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> create(@Valid @RequestBody CreateOrderRequest req) {
        return service.create(req)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @GetMapping("/{id}")
    public Mono<OrderResponse> get(@PathVariable Long id) {
        return service.getById(id);
    }
}

