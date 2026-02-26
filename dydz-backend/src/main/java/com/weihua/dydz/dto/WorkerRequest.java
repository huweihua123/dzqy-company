package com.weihua.dydz.dto;

import com.weihua.dydz.domain.PayType;
import com.weihua.dydz.domain.WorkerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WorkerRequest(
        @NotBlank String name,
        @NotNull Long roleId,
        @NotNull PayType payType,
        Long groupId,
        String phone,
        WorkerStatus status,
        BigDecimal tonPrice,
        BigDecimal dailySalary,
        BigDecimal yearlySalary,
        String remark
) {
}
