package com.weihua.dydz.repository;

import com.weihua.dydz.domain.ProductionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProductionRecordRepository extends JpaRepository<ProductionRecord, Long> {
    List<ProductionRecord> findByWorkDateBetween(LocalDate start, LocalDate end);
}
