package com.weihua.dydz.dto;

import com.weihua.dydz.domain.SalaryRuleType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SalaryRuleRequest(
        @NotNull Long roleId,
        @NotNull SalaryRuleType ruleType,
        BigDecimal pricePerTon,
        BigDecimal dailySalary,
        BigDecimal hourlySalary
) {
}
