package com.challenge.order.client;

import com.challenge.order.api.dto.ProductSnapshot;
import com.challenge.order.api.error.ProductClientException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class WebClientProductClient implements ProductClient {

    private final WebClient webClient;

    public WebClientProductClient(WebClient productWebClient) {
        this.webClient = productWebClient;
    }

    @Override
    public Mono<ProductSnapshot> getById(Long id) {
        return webClient.get()
                .uri("/products/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new ProductClientException("PRODUCT_4XX", "Producto no encontrado o request inválido")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new ProductClientException("PRODUCT_5XX", "Product service caído")))
                .bodyToMono(ProductSnapshot.class)
                .timeout(Duration.ofSeconds(2));
    }
}
