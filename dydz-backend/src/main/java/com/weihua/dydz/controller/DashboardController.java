package com.weihua.dydz.controller;

import com.weihua.dydz.domain.Attendance;
import com.weihua.dydz.domain.AttendanceStatus;
import com.weihua.dydz.domain.ProductionRecord;
import com.weihua.dydz.dto.DailyOverview;
import com.weihua.dydz.repository.AttendanceRepository;
import com.weihua.dydz.repository.ProductionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final AttendanceRepository attendanceRepository;
    private final ProductionRecordRepository productionRecordRepository;

    @GetMapping("/daily")
    public List<DailyOverview> daily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long groupId
    ) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before to date");
        }

        List<Attendance> attendances = attendanceRepository.findByWorkDateBetween(from, to);
        Map<LocalDate, int[]> dayCounts = new HashMap<>();
        for (Attendance a : attendances) {
            if (groupId != null) {
                if (a.getWorker().getGroup() == null || !groupId.equals(a.getWorker().getGroup().getId())) {
                    continue;
                }
            }
            int[] counts = dayCounts.computeIfAbsent(a.getWorkDate(), d -> new int[3]);
            if (a.getStatus() == AttendanceStatus.PRESENT) {
                counts[0]++;
            } else if (a.getStatus() == AttendanceStatus.LEAVE) {
                counts[1]++;
            } else if (a.getStatus() == AttendanceStatus.FACTORY_HOLIDAY) {
                counts[2]++;
            }
        }

        List<ProductionRecord> records = productionRecordRepository.findByWorkDateBetween(from, to);
        Map<LocalDate, BigDecimal> dayProduction = new HashMap<>();
        for (ProductionRecord r : records) {
            if (groupId != null) {
                if (r.getWorker() == null || r.getWorker().getGroup() == null
                        || !groupId.equals(r.getWorker().getGroup().getId())) {
                    continue;
                }
            }
            dayProduction.merge(r.getWorkDate(),
                    r.getNetTonnage() == null ? BigDecimal.ZERO : r.getNetTonnage(),
                    BigDecimal::add);
        }

        return from.datesUntil(to.plusDays(1))
                .map(date -> {
                    int[] counts = dayCounts.getOrDefault(date, new int[3]);
                    BigDecimal prod = dayProduction.getOrDefault(date, BigDecimal.ZERO);
                    return new DailyOverview(date, counts[0], counts[1], counts[2], prod);
                })
                .toList();
    }
}
