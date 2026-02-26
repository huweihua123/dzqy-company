package com.weihua.dydz.repository;

import com.weihua.dydz.domain.OwnerReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OwnerReceiptRepository extends JpaRepository<OwnerReceipt, Long> {
    List<OwnerReceipt> findByOwnerId(Long ownerId);
    List<OwnerReceipt> findByOwnerIdAndPayDateBetween(Long ownerId, LocalDate start, LocalDate end);
    List<OwnerReceipt> findByPayDateBetween(LocalDate start, LocalDate end);
}
