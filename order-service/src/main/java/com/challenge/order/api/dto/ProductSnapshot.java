package com.challenge.order.api.dto;

import java.math.BigDecimal;

public record ProductSnapshot(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        Integer stock
) {}

