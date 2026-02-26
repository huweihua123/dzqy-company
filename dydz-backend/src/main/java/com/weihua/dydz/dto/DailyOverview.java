package com.weihua.dydz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyOverview(
        LocalDate date,
        int presentCount,
        int leaveCount,
        int holidayCount,
        BigDecimal productionNet
) {
}
