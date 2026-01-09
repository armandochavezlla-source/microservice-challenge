package com.challenge.order.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customer,
        LocalDateTime createdAt,
        BigDecimal total,
        List<OrderItemResponse> items
) {}

