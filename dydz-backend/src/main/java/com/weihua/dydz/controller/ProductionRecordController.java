package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Order;
import com.weihua.dydz.domain.OrderItem;
import com.weihua.dydz.domain.ProductionRecord;
import com.weihua.dydz.domain.Worker;
import com.weihua.dydz.dto.ProductionRecordRequest;
import com.weihua.dydz.repository.OrderRepository;
import com.weihua.dydz.repository.OrderItemRepository;
import com.weihua.dydz.repository.ProductionRecordRepository;
import com.weihua.dydz.repository.WorkerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/production-records")
@RequiredArgsConstructor
public class ProductionRecordController {
    private final ProductionRecordRepository productionRecordRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final WorkerRepository workerRepository;

    @GetMapping
    public List<ProductionRecord> list() {
        return productionRecordRepository.findAll();
    }

    @PostMapping
    public ProductionRecord create(@Valid @RequestBody ProductionRecordRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        OrderItem item = orderItemRepository.findById(request.orderItemId())
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));
        Worker worker = workerRepository.findById(request.workerId())
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));
        if (!item.getOrder().getId().equals(order.getId())) {
            throw new IllegalArgumentException("Order item does not belong to order");
        }
        if (request.pieceCount().signum() <= 0) {
            throw new IllegalArgumentException("Piece count must be greater than 0");
        }

        BigDecimal multiplier = item.getMaterial() != null
                && item.getMaterial().getMultiplier() != null
                ? item.getMaterial().getMultiplier()
                : BigDecimal.ONE;
        BigDecimal gross = request.pieceCount().multiply(item.getPieceWeight()).multiply(multiplier);
        BigDecimal net = gross.multiply(new BigDecimal("0.9"));

        ProductionRecord record = new ProductionRecord();
        record.setOrder(order);
        record.setOrderItem(item);
        record.setWorker(worker);
        record.setWorkDate(request.workDate());
        record.setGrossTonnage(gross);
        record.setNetTonnage(net);
        record.setPieceCount(request.pieceCount());
        ProductionRecord saved = productionRecordRepository.save(record);

        item.setDonePieceCount(item.getDonePieceCount().add(request.pieceCount()));
        item.setDoneGrossTonnage(item.getDoneGrossTonnage().add(gross));
        item.setDoneNetTonnage(item.getDoneNetTonnage().add(net));
        orderItemRepository.save(item);

        order.setDoneGrossTonnage(order.getDoneGrossTonnage().add(gross));
        order.setDoneNetTonnage(order.getDoneNetTonnage().add(net));
        order.setDonePieceCount(order.getDonePieceCount().add(request.pieceCount()));
        if (order.getDoneGrossTonnage().compareTo(BigDecimal.ZERO) > 0) {
            order.setProductionStatus(com.weihua.dydz.domain.ProductionStatus.IN_PROGRESS);
        }
        if (order.getDoneGrossTonnage().compareTo(order.getPlanGrossTonnage()) >= 0) {
            order.setProductionStatus(com.weihua.dydz.domain.ProductionStatus.COMPLETED);
        }
        orderRepository.save(order);
        return saved;
    }
}
