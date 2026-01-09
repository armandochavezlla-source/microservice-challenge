package com.challenge.product.api;

import com.challenge.product.api.dto.CreateProductRequest;
import com.challenge.product.api.dto.ProductResponse;
import com.challenge.product.api.dto.UpdateProductRequest;
import com.challenge.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Mono<ProductResponse> get(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public Mono<List<ProductResponse>> list() {
        return service.listSortedByName();
    }

    @PostMapping
    public Mono<ResponseEntity<ProductResponse>> create(@Valid @RequestBody CreateProductRequest req) {
        return service.create(req)
                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
    }

    @PutMapping("/{id}")
    public Mono<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return service.delete(id).thenReturn(ResponseEntity.noContent().build());
    }
}

