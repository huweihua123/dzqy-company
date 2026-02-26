package com.weihua.dydz.dto;

import java.math.BigDecimal;

public record OwnerReportPoint(
        String ownerName,
        BigDecimal totalFee
) {
}
