package com.challenge.order.api;

import com.challenge.order.api.dto.CreateOrderItemRequest;
import com.challenge.order.api.dto.CreateOrderRequest;
import com.challenge.order.api.dto.OrderResponse;
import com.challenge.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired WebTestClient webTestClient;
    @MockBean OrderService service;

    @Test
    void create_ok() {
        var req = new CreateOrderRequest("Alice", List.of(new CreateOrderItemRequest(1L, 1)));

        var resp = new OrderResponse(1L, "Alice", LocalDateTime.now(), new BigDecimal("10.00"), List.of());

        when(service.create(req)).thenReturn(Mono.just(resp));

        webTestClient.post().uri("/orders")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.customer").isEqualTo("Alice");
    }
}

