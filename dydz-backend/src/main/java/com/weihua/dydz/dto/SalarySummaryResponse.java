package com.weihua.dydz.dto;

import com.weihua.dydz.domain.PayType;

import java.math.BigDecimal;

public record SalarySummaryResponse(
        Long workerId,
        String workerName,
        String role,
        PayType payType,
        BigDecimal earnedAmount,
        BigDecimal paidAmount,
        BigDecimal owedAmount
) {
}
