package com.weihua.dydz.dto;

import com.weihua.dydz.domain.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AttendanceRequest(
        @NotNull Long workerId,
        @NotNull LocalDate workDate,
        @NotNull AttendanceStatus status,
        String remark
) {
}
