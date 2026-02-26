package com.weihua.dydz.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WriteoffRequest(
        @NotNull LocalDate writeoffDate,
        @NotNull BigDecimal amount,
        String remark
) {
}
