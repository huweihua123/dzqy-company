package com.weihua.dydz.dto;

import com.weihua.dydz.domain.Order;
import com.weihua.dydz.domain.OrderItem;

import java.util.List;

public record OrderDetailResponse(
        Order order,
        List<OrderItem> items
) {
}
