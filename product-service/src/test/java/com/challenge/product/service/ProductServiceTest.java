package com.challenge.product.service;

import com.challenge.product.api.error.NotFoundException;
import com.challenge.product.domain.Product;
import com.challenge.product.repo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository repo;
    @InjectMocks ProductService service;

    @Test
    void getById_ok() {
        when(repo.findById(1L)).thenReturn(Mono.just(new Product(1L, "SKU-1", "Keyboard", new BigDecimal("10.00"), 5)));

        StepVerifier.create(service.getById(1L))
                .assertNext(resp -> {
                    assertThat(resp.id()).isEqualTo(1L);
                    assertThat(resp.name()).isEqualTo("Keyboard");
                })
                .verifyComplete();
    }

    @Test
    void getById_notFound() {
        when(repo.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(service.getById(99L))
                .expectErrorSatisfies(ex -> assertThat(ex).isInstanceOf(NotFoundException.class))
                .verify();
    }
}
