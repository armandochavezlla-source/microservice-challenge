package com.challenge.order.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customer,
        @NotEmpty List<CreateOrderItemRequest> items
) {}

