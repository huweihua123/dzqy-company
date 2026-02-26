package com.weihua.dydz.repository;

import com.weihua.dydz.domain.WorkerGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerGroupRepository extends JpaRepository<WorkerGroup, Long> {
    boolean existsByName(String name);
}
