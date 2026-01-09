package com.challenge.product.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("products")
public record Product(
        @Id Long id,
        String sku,
        String name,
        BigDecimal price,
        Integer stock
) {}

