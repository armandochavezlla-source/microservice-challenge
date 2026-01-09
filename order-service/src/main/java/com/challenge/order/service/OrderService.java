package com.challenge.order.service;

import com.challenge.order.api.dto.*;
import com.challenge.order.api.error.DomainException;
import com.challenge.order.client.ProductClient;
import com.challenge.order.domain.OrderEntity;
import com.challenge.order.domain.OrderItemEntity;
import com.challenge.order.repo.OrderItemRepository;
import com.challenge.order.repo.OrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepo, OrderItemRepository itemRepo, ProductClient productClient) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
        this.productClient = productClient;
    }

    private static final Predicate<CreateOrderItemRequest> QTY_OK =
            item -> item.quantity() != null && item.quantity() > 0;

    private static final Consumer<OrderLine> DEBUG_LINE =
            line -> System.out.println("[DEBUG] line=" + line);

    public Mono<OrderResponse> create(CreateOrderRequest req) {
        // Optional (obligatorio) + Supplier (obligatorio)
        Supplier<DomainException> customerRequired =
                () -> new DomainException("CUSTOMER_REQUIRED", "customer es obligatorio");

        String customer = Optional.ofNullable(req.customer())
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .orElseThrow(customerRequired);

        List<CreateOrderItemRequest> items = Optional.ofNullable(req.items())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new DomainException("ITEMS_REQUIRED", "items es obligatorio"));

        // Streams (obligatorio)
        if (!items.stream().allMatch(QTY_OK)) {
            return Mono.error(new DomainException("INVALID_QUANTITY", "quantity debe ser >= 1"));
        }

        return Flux.fromIterable(items)
                .flatMap(item ->
                        productClient.getById(item.productId())
                                .flatMap(product -> validateAndBuildLine(item, product))
                )
                .collectList()
                .flatMap(lines -> persist(customer, lines))
                // Manejo de errores en flujo reactivo (obligatorio)
                .onErrorMap(ex -> ex); // (aquí podrías mapear excepciones externas a DomainException si quieres)
    }

    public Mono<OrderResponse> getById(Long orderId) {
        Supplier<DomainException> notFound =
                () -> new DomainException("ORDER_NOT_FOUND", "No existe orden id=" + orderId);

        Mono<OrderEntity> orderMono = orderRepo.findById(orderId).switchIfEmpty(Mono.error(notFound));
        Mono<List<OrderItemEntity>> itemsMono = itemRepo.findByOrderId(orderId).collectList();

        return Mono.zip(orderMono, itemsMono)
                .map(tuple -> toResponse(tuple.getT1(), tuple.getT2()));
    }

    private Mono<OrderLine> validateAndBuildLine(CreateOrderItemRequest item, ProductSnapshot product) {
        Supplier<DomainException> noStock =
                () -> new DomainException("NO_STOCK", "Stock insuficiente para productId=" + product.id());

        int requested = item.quantity();
        int available = Optional.ofNullable(product.stock()).orElse(0);

        if (requested > available) {
            return Mono.error(noStock);
        }

        BigDecimal unit = product.price();
        BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(requested));

        return Mono.just(new OrderLine(product.id(), product.name(), requested, unit, lineTotal));
    }

    private Mono<OrderResponse> persist(String customer, List<OrderLine> lines) {
        // Consumer (obligatorio) (debug simple)
        lines.forEach(DEBUG_LINE);

        // Streams (obligatorio): sumar totales
        BigDecimal total = lines.stream()
                .map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity toSave = new OrderEntity(null, customer, LocalDateTime.now(), total);

        return orderRepo.save(toSave)
                .flatMap(savedOrder -> {
                    List<OrderItemEntity> itemEntities = lines.stream()
                            .map(line -> new OrderItemEntity(
                                    null,
                                    savedOrder.id(),
                                    line.productId(),
                                    line.productName(),
                                    line.quantity(),
                                    line.unitPrice(),
                                    line.lineTotal()
                            ))
                            .toList();

                    return Flux.fromIterable(itemEntities)
                            .flatMap(itemRepo::save)
                            .collectList()
                            .map(savedItems -> toResponse(savedOrder, savedItems));
                });
    }

    private OrderResponse toResponse(OrderEntity order, List<OrderItemEntity> items) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(i -> new OrderItemResponse(i.productId(), i.productName(), i.quantity(), i.unitPrice(), i.lineTotal()))
                .toList();

        return new OrderResponse(order.id(), order.customer(), order.createdAt(), order.total(), itemResponses);
    }

    private record OrderLine(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {}
}

