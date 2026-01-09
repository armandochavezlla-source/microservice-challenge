package com.challenge.product.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(0) Integer stock
) {}

