package com.weihua.dydz.controller;

import com.weihua.dydz.domain.SalaryPayment;
import com.weihua.dydz.dto.SalaryPaymentRequest;
import com.weihua.dydz.repository.SalaryPaymentRepository;
import com.weihua.dydz.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary-payments")
@RequiredArgsConstructor
public class SalaryPaymentController {
    private final SalaryPaymentRepository salaryPaymentRepository;
    private final SalaryService salaryService;

    @GetMapping
    public List<SalaryPayment> list() {
        return salaryPaymentRepository.findAll();
    }

    @PostMapping
    public SalaryPayment create(@Valid @RequestBody SalaryPaymentRequest request) {
        return salaryService.pay(request);
    }
}
