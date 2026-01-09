package com.challenge.order.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("orders")
public record OrderEntity(
        @Id Long id,
        String customer,
        @Column("created_at") LocalDateTime createdAt,
        BigDecimal total
) {}

