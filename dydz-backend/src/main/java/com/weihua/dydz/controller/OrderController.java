package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Order;
import com.weihua.dydz.domain.ProductionStatus;
import com.weihua.dydz.domain.ReceivableWriteoff;
import com.weihua.dydz.dto.OrderCreateRequest;
import com.weihua.dydz.dto.OrderDetailResponse;
import com.weihua.dydz.dto.WriteoffRequest;
import com.weihua.dydz.repository.OrderItemRepository;
import com.weihua.dydz.repository.OrderRepository;
import com.weihua.dydz.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;

    @GetMapping
    public List<Order> list(
            @RequestParam(required = false) ProductionStatus productionStatus,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Specification<Order> spec = Specification.where(null);
        if (productionStatus != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("productionStatus"), productionStatus));
        }
        if (ownerId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId));
        }
        if (orderNo != null && !orderNo.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("orderNo"), "%" + orderNo.trim() + "%"));
        }
        if (from != null && to != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("orderDate"), from, to));
        }
        if (spec == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findAll(spec);
    }

    @GetMapping("/{id}")
    public OrderDetailResponse detail(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return new OrderDetailResponse(order, orderItemRepository.findByOrderId(id));
    }

    @PostMapping
    public Order create(@Valid @RequestBody OrderCreateRequest request) {
        return orderService.create(request);
    }

    @PostMapping("/{id}/writeoff")
    public ReceivableWriteoff writeoff(@PathVariable Long id, @Valid @RequestBody WriteoffRequest request) {
        return orderService.writeoff(id, request);
    }
}
