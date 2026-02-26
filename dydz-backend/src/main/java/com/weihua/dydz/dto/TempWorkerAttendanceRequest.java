package com.weihua.dydz.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TempWorkerAttendanceRequest(
        @NotNull LocalDate workDate,
        @NotNull TempWorkerRequest worker
) {
}
