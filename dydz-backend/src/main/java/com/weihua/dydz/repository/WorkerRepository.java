package com.weihua.dydz.repository;

import com.weihua.dydz.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    boolean existsByName(String name);
    java.util.List<Worker> findByGroupId(Long groupId);
}
