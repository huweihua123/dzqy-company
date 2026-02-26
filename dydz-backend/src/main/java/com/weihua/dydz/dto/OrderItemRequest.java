package com.weihua.dydz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemRequest(
        @NotBlank String productName,
        Long materialId,
        Long specId,
        @NotNull BigDecimal pieceCount,
        @NotNull BigDecimal pieceWeight
) {
}
