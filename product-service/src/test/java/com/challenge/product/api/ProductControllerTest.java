package com.challenge.product.api;

import com.challenge.product.api.dto.ProductResponse;
import com.challenge.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired WebTestClient webTestClient;
    @MockBean ProductService service;

    @Test
    void getProduct_ok() {
        when(service.getById(1L)).thenReturn(Mono.just(new ProductResponse(1L, "SKU-1", "Keyboard", new BigDecimal("10.00"), 5)));

        webTestClient.get().uri("/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Keyboard");
    }
}
