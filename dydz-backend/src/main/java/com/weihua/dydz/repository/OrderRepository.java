package com.weihua.dydz.repository;

import com.weihua.dydz.domain.Order;
import com.weihua.dydz.domain.ProductionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByProductionStatus(ProductionStatus productionStatus);
    List<Order> findByOrderDateBetween(LocalDate start, LocalDate end);
    boolean existsByOrderNo(String orderNo);
}
