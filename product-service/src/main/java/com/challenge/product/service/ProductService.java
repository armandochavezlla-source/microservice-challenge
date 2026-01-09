package com.challenge.product.service;

import com.challenge.product.api.dto.CreateProductRequest;
import com.challenge.product.api.dto.ProductResponse;
import com.challenge.product.api.dto.UpdateProductRequest;
import com.challenge.product.api.error.NotFoundException;
import com.challenge.product.domain.Product;
import com.challenge.product.repo.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public Mono<ProductResponse> getById(Long id) {
        Supplier<NotFoundException> notFound =
                () -> new NotFoundException("PRODUCT_NOT_FOUND", "No existe producto id=" + id);

        return repo.findById(id)
                .switchIfEmpty(Mono.error(notFound))
                .map(this::toResponse);
    }

    public Mono<List<ProductResponse>> listSortedByName() {
        // Streams (obligatorio): orden + map + toList
        return repo.findAll()
                .collectList()
                .map(list -> list.stream()
                        .sorted(Comparator.comparing(Product::name, String.CASE_INSENSITIVE_ORDER))
                        .map(this::toResponse)
                        .toList());
    }

    public Mono<ProductResponse> create(CreateProductRequest req) {
        // Optional (obligatorio): evita null y normaliza
        String sku = Optional.ofNullable(req.sku()).orElse("").trim();
        String name = Optional.ofNullable(req.name()).orElse("").trim();

        Product toSave = new Product(null, sku, name, req.price(), req.stock());
        return repo.save(toSave).map(this::toResponse);
    }

    public Mono<ProductResponse> update(Long id, UpdateProductRequest req) {
        Supplier<NotFoundException> notFound =
                () -> new NotFoundException("PRODUCT_NOT_FOUND", "No existe producto id=" + id);

        return repo.findById(id)
                .switchIfEmpty(Mono.error(notFound))
                .map(current -> new Product(
                        current.id(),
                        current.sku(),
                        req.name().trim(),
                        req.price(),
                        req.stock()
                ))
                .flatMap(repo::save)
                .map(this::toResponse);
    }

    public Mono<Void> delete(Long id) {
        return repo.deleteById(id);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.id(), p.sku(), p.name(), p.price(), p.stock());
    }
}
