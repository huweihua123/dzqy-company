package com.weihua.dydz.service;

import com.weihua.dydz.domain.AccountLog;
import com.weihua.dydz.domain.AccountLogType;
import com.weihua.dydz.domain.SalaryPayment;
import com.weihua.dydz.domain.Worker;
import com.weihua.dydz.dto.SalaryPaymentRequest;
import com.weihua.dydz.repository.AccountLogRepository;
import com.weihua.dydz.repository.SalaryPaymentRepository;
import com.weihua.dydz.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SalaryService {
    private final SalaryPaymentRepository salaryPaymentRepository;
    private final AccountLogRepository accountLogRepository;
    private final WorkerRepository workerRepository;

    @Transactional
    public SalaryPayment pay(SalaryPaymentRequest request) {
        Worker worker = workerRepository.findById(request.workerId())
                .orElseThrow(() -> new IllegalArgumentException("Worker not found"));

        SalaryPayment payment = new SalaryPayment();
        payment.setWorker(worker);
        payment.setPayDate(request.payDate());
        payment.setAmount(request.amount());
        payment.setRemark(request.remark());
        SalaryPayment savedPayment = salaryPaymentRepository.save(payment);

        BigDecimal lastBalance = accountLogRepository
                .findTopByWorkerIdOrderByCreatedAtDesc(worker.getId())
                .map(AccountLog::getBalance)
                .orElse(BigDecimal.ZERO);
        BigDecimal newBalance = lastBalance.add(request.amount());

        AccountLog log = new AccountLog();
        log.setWorker(worker);
        log.setType(AccountLogType.SALARY_PAYMENT);
        log.setChangeAmount(request.amount());
        log.setBalance(newBalance);
        accountLogRepository.save(log);

        return savedPayment;
    }
}
