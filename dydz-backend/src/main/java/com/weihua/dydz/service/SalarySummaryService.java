package com.weihua.dydz.service;

import com.weihua.dydz.domain.Attendance;
import com.weihua.dydz.domain.AttendanceStatus;
import com.weihua.dydz.domain.PayType;
import com.weihua.dydz.domain.ProductionRecord;
import com.weihua.dydz.domain.SalaryPayment;
import com.weihua.dydz.domain.Worker;
import com.weihua.dydz.dto.SalarySummaryResponse;
import com.weihua.dydz.repository.AttendanceRepository;
import com.weihua.dydz.repository.ProductionRecordRepository;
import com.weihua.dydz.repository.SalaryPaymentRepository;
import com.weihua.dydz.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalarySummaryService {
    private final WorkerRepository workerRepository;
    private final AttendanceRepository attendanceRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final SalaryPaymentRepository salaryPaymentRepository;

    public List<SalarySummaryResponse> summary(LocalDate from, LocalDate to) {
        List<Worker> workers = workerRepository.findAll();

        List<Attendance> attendances = (from == null || to == null)
                ? attendanceRepository.findAll()
                : attendanceRepository.findByWorkDateBetween(from, to);

        List<ProductionRecord> productions = (from == null || to == null)
                ? productionRecordRepository.findAll()
                : productionRecordRepository.findByWorkDateBetween(from, to);

        List<SalaryPayment> payments = (from == null || to == null)
                ? salaryPaymentRepository.findAll()
                : salaryPaymentRepository.findByPayDateBetween(from, to);

        Map<LocalDate, BigDecimal> dayNetTonnage = new HashMap<>();
        for (ProductionRecord record : productions) {
            BigDecimal net = record.getNetTonnage() == null ? BigDecimal.ZERO : record.getNetTonnage();
            dayNetTonnage.merge(record.getWorkDate(), net, BigDecimal::add);
        }

        Map<Long, Integer> presentDays = new HashMap<>();
        Map<Long, BigDecimal> pieceEarned = new HashMap<>();

        for (Attendance attendance : attendances) {
            if (attendance.getStatus() != AttendanceStatus.PRESENT) {
                continue;
            }
            Long workerId = attendance.getWorker().getId();
            presentDays.merge(workerId, 1, Integer::sum);

            BigDecimal dayNet = dayNetTonnage.getOrDefault(attendance.getWorkDate(), BigDecimal.ZERO);
            pieceEarned.merge(workerId, dayNet, BigDecimal::add);
        }

        Map<Long, BigDecimal> paidAmount = new HashMap<>();
        for (SalaryPayment payment : payments) {
            paidAmount.merge(payment.getWorker().getId(), payment.getAmount(), BigDecimal::add);
        }

        return workers.stream().map(worker -> {
            BigDecimal earned = BigDecimal.ZERO;
            PayType payType = worker.getPayType();
            int days = presentDays.getOrDefault(worker.getId(), 0);

            if (payType == PayType.PIECE) {
                BigDecimal tonPrice = worker.getTonPrice() == null ? BigDecimal.ZERO : worker.getTonPrice();
                BigDecimal netTotal = pieceEarned.getOrDefault(worker.getId(), BigDecimal.ZERO);
                earned = netTotal.multiply(tonPrice);
            } else if (payType == PayType.DAILY) {
                BigDecimal daily = worker.getDailySalary() == null ? BigDecimal.ZERO : worker.getDailySalary();
                earned = daily.multiply(BigDecimal.valueOf(days));
            } else if (payType == PayType.YEARLY) {
                BigDecimal yearly = worker.getYearlySalary() == null ? BigDecimal.ZERO : worker.getYearlySalary();
                if (from == null || to == null) {
                    earned = yearly;
                } else if (isFullYear(from, to)) {
                    earned = yearly;
                } else {
                    earned = BigDecimal.ZERO;
                }
            }

            BigDecimal paid = paidAmount.getOrDefault(worker.getId(), BigDecimal.ZERO);
            BigDecimal owed = earned.subtract(paid);

            return new SalarySummaryResponse(
                    worker.getId(),
                    worker.getName(),
                    worker.getRole().getName(),
                    worker.getPayType(),
                    earned,
                    paid,
                    owed
            );
        }).toList();
    }

    private boolean isFullYear(LocalDate from, LocalDate to) {
        if (from.getYear() != to.getYear()) {
            return false;
        }
        return from.getMonth() == Month.JANUARY
                && from.getDayOfMonth() == 1
                && to.getMonth() == Month.DECEMBER
                && to.getDayOfMonth() == 31;
    }
}
