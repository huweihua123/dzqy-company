package com.weihua.dydz.dto;

import com.weihua.dydz.domain.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BatchAttendanceRangeRequest(
        @NotNull LocalDate from,
        @NotNull LocalDate to,
        @NotNull Long groupId,
        @NotNull AttendanceStatus status
) {
}
