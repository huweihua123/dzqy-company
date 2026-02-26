package com.weihua.dydz.dto;

import java.math.BigDecimal;

public record OwnerBalanceResponse(
        Long ownerId,
        String ownerName,
        BigDecimal totalReceivable,
        BigDecimal totalReceived,
        BigDecimal totalOutstanding
) {
}
