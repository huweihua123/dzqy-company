package com.weihua.dydz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReportPoint(
        LocalDate date,
        BigDecimal value
) {
}
