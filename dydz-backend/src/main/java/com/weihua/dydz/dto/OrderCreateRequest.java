package com.weihua.dydz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderCreateRequest(
        @NotBlank String orderNo,
        @NotNull Long ownerId,
        @NotNull LocalDate orderDate,
        @NotEmpty @Valid List<OrderItemRequest> items
) {
}
