package com.weihua.dydz.service;

import com.weihua.dydz.domain.Order;
import com.weihua.dydz.domain.Owner;
import com.weihua.dydz.domain.OwnerReceipt;
import com.weihua.dydz.dto.OwnerBalanceResponse;
import com.weihua.dydz.repository.OrderRepository;
import com.weihua.dydz.repository.OwnerReceiptRepository;
import com.weihua.dydz.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerBalanceService {
    private final OwnerRepository ownerRepository;
    private final OrderRepository orderRepository;
    private final OwnerReceiptRepository ownerReceiptRepository;

    public List<OwnerBalanceResponse> summary(LocalDate from, LocalDate to) {
        List<Owner> owners = ownerRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        List<OwnerReceipt> receipts = ownerReceiptRepository.findAll();

        Map<Long, BigDecimal> orderTotals = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getOwner().getId(),
                        Collectors.reducing(BigDecimal.ZERO, Order::getTotalFee, BigDecimal::add)));

        Map<Long, BigDecimal> receiptTotals = receipts.stream()
                .collect(Collectors.groupingBy(r -> r.getOwner().getId(),
                        Collectors.reducing(BigDecimal.ZERO, OwnerReceipt::getAmount, BigDecimal::add)));

        return owners.stream().map(owner -> {
            BigDecimal receivable = orderTotals.getOrDefault(owner.getId(), BigDecimal.ZERO);
            BigDecimal received = receiptTotals.getOrDefault(owner.getId(), BigDecimal.ZERO);
            BigDecimal outstanding = receivable.subtract(received);
            return new OwnerBalanceResponse(owner.getId(), owner.getName(), receivable, received, outstanding);
        }).toList();
    }
}
