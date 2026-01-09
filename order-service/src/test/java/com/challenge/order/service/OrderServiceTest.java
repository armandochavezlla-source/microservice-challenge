package com.challenge.order.service;

import com.challenge.order.api.dto.CreateOrderItemRequest;
import com.challenge.order.api.dto.CreateOrderRequest;
import com.challenge.order.api.dto.ProductSnapshot;
import com.challenge.order.client.ProductClient;
import com.challenge.order.domain.OrderEntity;
import com.challenge.order.domain.OrderItemEntity;
import com.challenge.order.repo.OrderItemRepository;
import com.challenge.order.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepo;
    @Mock OrderItemRepository itemRepo;
    @Mock ProductClient productClient;

    @InjectMocks OrderService service;

    @Test
    void create_ok() {
        when(productClient.getById(1L)).thenReturn(Mono.just(new ProductSnapshot(1L, "SKU-1", "Keyboard", new BigDecimal("10.00"), 10)));
        when(orderRepo.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity o = inv.getArgument(0);
            return Mono.just(new OrderEntity(100L, o.customer(), LocalDateTime.now(), o.total()));
        });
        when(itemRepo.save(any(OrderItemEntity.class))).thenAnswer(inv -> {
            OrderItemEntity i = inv.getArgument(0);
            return Mono.just(new OrderItemEntity(200L, i.orderId(), i.productId(), i.productName(), i.quantity(), i.unitPrice(), i.lineTotal()));
        });

        var req = new CreateOrderRequest("Alice", List.of(new CreateOrderItemRequest(1L, 2)));

        StepVerifier.create(service.create(req))
                .assertNext(resp -> {
                    assertThat(resp.id()).isEqualTo(100L);
                    assertThat(resp.total()).isEqualByComparingTo("20.00");
                    assertThat(resp.items()).hasSize(1);
                })
                .verifyComplete();
    }
}

