package com.weihua.dydz.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BatchAttendanceRequest(
        @NotNull LocalDate workDate,
        @NotNull Long groupId,
        @NotNull com.weihua.dydz.domain.AttendanceStatus status
) {
}
