package com.weihua.dydz.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductionRecordRequest(
        @NotNull Long orderId,
        @NotNull Long orderItemId,
        @NotNull Long workerId,
        @NotNull LocalDate workDate,
        @NotNull BigDecimal pieceCount
) {
}
