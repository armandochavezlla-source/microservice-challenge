package com.challenge.product.repo;

import com.challenge.product.domain.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Mono<Product> findBySku(String sku);
}

