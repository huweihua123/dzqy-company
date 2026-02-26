package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Order;
import com.weihua.dydz.domain.ProductionRecord;
import com.weihua.dydz.dto.ReportPoint;
import com.weihua.dydz.dto.SalarySummaryResponse;
import com.weihua.dydz.dto.OwnerReportPoint;
import com.weihua.dydz.repository.OrderRepository;
import com.weihua.dydz.repository.ProductionRecordRepository;
import com.weihua.dydz.service.ReportExportService;
import com.weihua.dydz.service.SalarySummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {
    private final ProductionRecordRepository productionRecordRepository;
    private final OrderRepository orderRepository;
    private final SalarySummaryService salarySummaryService;
    private final ReportExportService reportExportService;

    @GetMapping("/production")
    public List<ReportPoint> production(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "day") String granularity
    ) {
        List<ProductionRecord> records = productionRecordRepository.findByWorkDateBetween(from, to);
        Map<LocalDate, BigDecimal> map = new TreeMap<>();
        for (ProductionRecord r : records) {
            LocalDate key = bucketDate(r.getWorkDate(), granularity);
            map.merge(key, r.getNetTonnage(), BigDecimal::add);
        }
        return map.entrySet().stream().map(e -> new ReportPoint(e.getKey(), e.getValue())).toList();
    }

    @GetMapping("/orders")
    public List<ReportPoint> orders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false, defaultValue = "day") String granularity
    ) {
        List<Order> orders = orderRepository.findByOrderDateBetween(from, to);
        Map<LocalDate, BigDecimal> map = new TreeMap<>();
        for (Order o : orders) {
            LocalDate key = bucketDate(o.getOrderDate(), granularity);
            map.merge(key, o.getTotalFee(), BigDecimal::add);
        }
        return map.entrySet().stream().map(e -> new ReportPoint(e.getKey(), e.getValue())).toList();
    }

    @GetMapping("/salary")
    public List<SalarySummaryResponse> salary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return salarySummaryService.summary(from, to);
    }

    @GetMapping("/owners")
    public List<OwnerReportPoint> owners(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<Order> orders = orderRepository.findByOrderDateBetween(from, to);
        Map<String, BigDecimal> map = new HashMap<>();
        for (Order o : orders) {
            String ownerName = o.getOwner().getName();
            map.merge(ownerName, o.getTotalFee(), BigDecimal::add);
        }
        return map.entrySet().stream()
                .map(e -> new OwnerReportPoint(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(OwnerReportPoint::totalFee).reversed())
                .toList();
    }

    private LocalDate bucketDate(LocalDate date, String granularity) {
        return switch (granularity) {
            case "week" -> date.with(java.time.temporal.WeekFields.ISO.getFirstDayOfWeek())
                    .minusDays((date.getDayOfWeek().getValue() + 6) % 7);
            case "month" -> date.withDayOfMonth(1);
            default -> date;
        };
    }

    @GetMapping(value = "/production.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public org.springframework.http.ResponseEntity<byte[]> productionXlsx(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        byte[] bytes = reportExportService.exportProduction(production(from, to, "day"));
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=production_report.xlsx")
                .body(bytes);
    }

    @GetMapping(value = "/orders.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public org.springframework.http.ResponseEntity<byte[]> ordersXlsx(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        byte[] bytes = reportExportService.exportOrders(orders(from, to, "day"));
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_report.xlsx")
                .body(bytes);
    }

    @GetMapping(value = "/salary.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public org.springframework.http.ResponseEntity<byte[]> salaryXlsx(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        byte[] bytes = reportExportService.exportSalary(salary(from, to));
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=salary_report.xlsx")
                .body(bytes);
    }

    @GetMapping(value = "/owners.xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public org.springframework.http.ResponseEntity<byte[]> ownersXlsx(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        byte[] bytes = reportExportService.exportOwners(owners(from, to));
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=owner_report.xlsx")
                .body(bytes);
    }
}
