package com.weihua.dydz.controller;

import com.weihua.dydz.dto.SalaryBatchPayItem;
import com.weihua.dydz.dto.SalaryBatchPayRequest;
import com.weihua.dydz.dto.SalarySummaryResponse;
import com.weihua.dydz.service.ReportExportService;
import com.weihua.dydz.service.SalarySummaryService;
import com.weihua.dydz.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryCalcController {
    private final SalarySummaryService salarySummaryService;
    private final SalaryService salaryService;
    private final ReportExportService reportExportService;

    @GetMapping("/calc")
    public List<SalarySummaryResponse> calc(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return salarySummaryService.summary(from, to);
    }

    @PostMapping("/batch-pay")
    public void batchPay(@Valid @RequestBody SalaryBatchPayRequest request) {
        for (SalaryBatchPayItem item : request.items()) {
            salaryService.pay(new com.weihua.dydz.dto.SalaryPaymentRequest(
                    item.workerId(), item.payDate(), item.amount(), item.remark()
            ));
        }
    }

    @GetMapping(value = "/calc.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public org.springframework.http.ResponseEntity<byte[]> calcXlsx(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        byte[] bytes = reportExportService.exportSalary(salarySummaryService.summary(from, to));
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=salary_calc.xlsx")
                .body(bytes);
    }
}
