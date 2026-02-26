package com.weihua.dydz.controller;

import com.weihua.dydz.domain.SalaryRule;
import com.weihua.dydz.domain.WorkerRole;
import com.weihua.dydz.dto.SalaryRuleRequest;
import com.weihua.dydz.repository.SalaryRuleRepository;
import com.weihua.dydz.repository.WorkerRoleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary-rules")
@RequiredArgsConstructor
public class SalaryRuleController {
    private final SalaryRuleRepository salaryRuleRepository;
    private final WorkerRoleRepository workerRoleRepository;

    @GetMapping
    public List<SalaryRule> list() {
        return salaryRuleRepository.findAll();
    }

    @PostMapping
    public SalaryRule upsert(@Valid @RequestBody SalaryRuleRequest request) {
        WorkerRole role = workerRoleRepository.findById(request.roleId())
                .orElseThrow(() -> new IllegalArgumentException("Worker role not found"));

        SalaryRule rule = salaryRuleRepository.findByRoleId(request.roleId())
                .orElseGet(SalaryRule::new);
        rule.setRole(role);
        rule.setRuleType(request.ruleType());
        rule.setPricePerTon(request.pricePerTon());
        rule.setDailySalary(request.dailySalary());
        rule.setHourlySalary(request.hourlySalary());
        return salaryRuleRepository.save(rule);
    }
}
