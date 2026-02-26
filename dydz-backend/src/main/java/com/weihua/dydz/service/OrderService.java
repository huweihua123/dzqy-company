package com.weihua.dydz.service;

import com.weihua.dydz.domain.*;
import com.weihua.dydz.dto.OrderCreateRequest;
import com.weihua.dydz.dto.OrderItemRequest;
import com.weihua.dydz.dto.WriteoffRequest;
import com.weihua.dydz.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OwnerRepository ownerRepository;
    private final MaterialRepository materialRepository;
    private final SpecRepository specRepository;
    private final ReceivableWriteoffRepository writeoffRepository;

    @Transactional
    public Order create(OrderCreateRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        if (owner.getUnitPrice() == null || owner.getUnitPrice().signum() <= 0) {
            throw new IllegalArgumentException("Owner unit price must be greater than 0");
        }

        Order order = new Order();
        order.setOrderNo(request.orderNo());
        order.setOwner(owner);
        order.setOrderDate(request.orderDate());

        BigDecimal planGross = BigDecimal.ZERO;
        BigDecimal planNet = BigDecimal.ZERO;
        BigDecimal planPiece = BigDecimal.ZERO;
        for (OrderItemRequest item : request.items()) {
            if (item.pieceCount().signum() <= 0 || item.pieceWeight().signum() <= 0) {
                throw new IllegalArgumentException("Piece count and piece weight must be greater than 0");
            }
            BigDecimal multiplier = BigDecimal.ONE;
            if (item.materialId() != null) {
                Material material = materialRepository.findById(item.materialId())
                        .orElseThrow(() -> new IllegalArgumentException("Material not found"));
                if (material.getMultiplier() != null) {
                    multiplier = material.getMultiplier();
                }
            }
            BigDecimal gross = item.pieceCount().multiply(item.pieceWeight()).multiply(multiplier);
            BigDecimal net = gross.multiply(new BigDecimal("0.9"));
            planGross = planGross.add(gross);
            planNet = planNet.add(net);
            planPiece = planPiece.add(item.pieceCount());
        }

        BigDecimal totalFee = BigDecimal.ZERO;
        order.setPlanGrossTonnage(planGross);
        order.setPlanNetTonnage(planNet);
        order.setPlanPieceCount(planPiece);
        order.setTotalFee(BigDecimal.ZERO);
        order.setReceivableAmount(BigDecimal.ZERO);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setOutstandingAmount(BigDecimal.ZERO);

        Order saved = orderRepository.save(order);
        for (OrderItemRequest itemReq : request.items()) {
            Material material = null;
            Spec spec = null;
            BigDecimal multiplier = BigDecimal.ONE;
            if (itemReq.materialId() != null) {
                material = materialRepository.findById(itemReq.materialId())
                        .orElseThrow(() -> new IllegalArgumentException("Material not found"));
                if (material.getMultiplier() != null) {
                    multiplier = material.getMultiplier();
                }
            }
            if (itemReq.specId() != null) {
                spec = specRepository.findById(itemReq.specId())
                        .orElseThrow(() -> new IllegalArgumentException("Spec not found"));
            }

            BigDecimal gross = itemReq.pieceCount().multiply(itemReq.pieceWeight()).multiply(multiplier);
            BigDecimal net = gross.multiply(new BigDecimal("0.9"));
            OrderItem item = new OrderItem();
            item.setOrder(saved);
            item.setProductName(itemReq.productName());
            item.setMaterial(material);
            item.setSpec(spec);
            item.setPieceCount(itemReq.pieceCount());
            item.setPieceWeight(itemReq.pieceWeight());
            item.setGrossTonnage(gross);
            item.setNetTonnage(net);
            BigDecimal amount = gross.multiply(owner.getUnitPrice());
            item.setAmount(amount);
            orderItemRepository.save(item);
            totalFee = totalFee.add(amount);
        }
        saved.setTotalFee(totalFee);
        saved.setReceivableAmount(totalFee);
        saved.setOutstandingAmount(totalFee);
        orderRepository.save(saved);
        return saved;
    }

    @Transactional
    public ReceivableWriteoff writeoff(Long orderId, WriteoffRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (request.amount().compareTo(order.getOutstandingAmount()) > 0) {
            throw new IllegalArgumentException("Writeoff amount exceeds outstanding amount");
        }

        ReceivableWriteoff writeoff = new ReceivableWriteoff();
        writeoff.setOrder(order);
        writeoff.setWriteoffDate(request.writeoffDate());
        writeoff.setAmount(request.amount());
        writeoff.setRemark(request.remark());
        ReceivableWriteoff saved = writeoffRepository.save(writeoff);

        BigDecimal newPaid = order.getPaidAmount().add(request.amount());
        BigDecimal newOutstanding = order.getOutstandingAmount().subtract(request.amount());
        order.setPaidAmount(newPaid);
        order.setOutstandingAmount(newOutstanding);
        if (newOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            order.setStatus(OrderStatus.PAID);
        } else if (newPaid.compareTo(BigDecimal.ZERO) > 0) {
            order.setStatus(OrderStatus.PARTIALLY_PAID);
        }
        orderRepository.save(order);

        return saved;
    }

}
