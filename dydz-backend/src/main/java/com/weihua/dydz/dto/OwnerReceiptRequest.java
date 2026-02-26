package com.weihua.dydz.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OwnerReceiptRequest(
        @NotNull Long ownerId,
        @NotNull LocalDate payDate,
        @NotNull BigDecimal amount,
        String remark
) {
}
