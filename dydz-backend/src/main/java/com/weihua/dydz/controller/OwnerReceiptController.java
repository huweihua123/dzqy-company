package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Owner;
import com.weihua.dydz.domain.OwnerReceipt;
import com.weihua.dydz.dto.OwnerBalanceResponse;
import com.weihua.dydz.dto.OwnerReceiptRequest;
import com.weihua.dydz.repository.OwnerReceiptRepository;
import com.weihua.dydz.repository.OwnerRepository;
import com.weihua.dydz.service.OwnerBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OwnerReceiptController {
    private final OwnerRepository ownerRepository;
    private final OwnerReceiptRepository ownerReceiptRepository;
    private final OwnerBalanceService ownerBalanceService;

    @GetMapping("/owner-receipts")
    public List<OwnerReceipt> list(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (ownerId != null && from != null && to != null) {
            return ownerReceiptRepository.findByOwnerIdAndPayDateBetween(ownerId, from, to);
        }
        if (ownerId != null) {
            return ownerReceiptRepository.findByOwnerId(ownerId);
        }
        if (from != null && to != null) {
            return ownerReceiptRepository.findByPayDateBetween(from, to);
        }
        return ownerReceiptRepository.findAll();
    }

    @PostMapping("/owner-receipts")
    public OwnerReceipt create(@Valid @RequestBody OwnerReceiptRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        OwnerReceipt receipt = new OwnerReceipt();
        receipt.setOwner(owner);
        receipt.setPayDate(request.payDate());
        receipt.setAmount(request.amount());
        receipt.setRemark(request.remark());
        return ownerReceiptRepository.save(receipt);
    }

    @PutMapping("/owner-receipts/{id}")
    public OwnerReceipt update(@PathVariable Long id, @Valid @RequestBody OwnerReceiptRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        OwnerReceipt receipt = ownerReceiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found"));
        receipt.setOwner(owner);
        receipt.setPayDate(request.payDate());
        receipt.setAmount(request.amount());
        receipt.setRemark(request.remark());
        return ownerReceiptRepository.save(receipt);
    }

    @DeleteMapping("/owner-receipts/{id}")
    public void delete(@PathVariable Long id) {
        ownerReceiptRepository.deleteById(id);
    }

    @GetMapping("/owner-balances")
    public List<OwnerBalanceResponse> balance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ownerBalanceService.summary(from, to);
    }
}
