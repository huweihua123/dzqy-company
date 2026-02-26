package com.weihua.dydz.repository;

import com.weihua.dydz.domain.SalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalaryPaymentRepository extends JpaRepository<SalaryPayment, Long> {
    List<SalaryPayment> findByPayDateBetween(LocalDate start, LocalDate end);
}
