package com.weihua.dydz.dto;

import com.weihua.dydz.domain.PayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TempWorkerRequest(
        @NotBlank String name,
        @NotNull Long roleId,
        @NotNull PayType payType,
        @NotNull BigDecimal dailySalary,
        String phone,
        String remark
) {
}
