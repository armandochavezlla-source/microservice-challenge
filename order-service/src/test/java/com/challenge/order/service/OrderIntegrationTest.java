package com.challenge.order.service;

import com.challenge.order.api.dto.CreateOrderItemRequest;
import com.challenge.order.api.dto.CreateOrderRequest;
import com.challenge.product.ProductServiceApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderIntegrationTest {

    static ConfigurableApplicationContext productCtx;
    static int productPort;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        productCtx = new SpringApplicationBuilder(ProductServiceApplication.class)
                .properties(
                        "server.port=0",
                        "spring.r2dbc.url=r2dbc:h2:mem:///productdb_it;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                        "spring.sql.init.mode=always"
                )
                .run();

        productPort = ((ReactiveWebServerApplicationContext) productCtx).getWebServer().getPort();
        registry.add("product.base-url", () -> "http://localhost:" + productPort);

        // Base de datos separada para order-service en integración:
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///orderdb_it;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
    }

    @AfterAll
    static void stop() {
        if (productCtx != null) productCtx.close();
    }

    @Autowired WebTestClient webTestClient;

    @Test
    void createOrder_talksToProductService() {
        // data.sql de product-service inserta productos; normalmente el primero queda con id=1
        var req = new CreateOrderRequest("Alice", List.of(new CreateOrderItemRequest(1L, 2)));

        webTestClient.post().uri("/orders")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.customer").isEqualTo("Alice")
                .jsonPath("$.items[0].productId").isEqualTo(1);
    }
}

