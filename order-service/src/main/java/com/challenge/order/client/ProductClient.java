package com.challenge.order.client;

import com.challenge.order.api.dto.ProductSnapshot;
import reactor.core.publisher.Mono;

public interface ProductClient {
    Mono<ProductSnapshot> getById(Long id);
}

