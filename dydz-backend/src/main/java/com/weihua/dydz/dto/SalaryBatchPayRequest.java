package com.weihua.dydz.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SalaryBatchPayRequest(
        @NotEmpty List<SalaryBatchPayItem> items
) {
}
