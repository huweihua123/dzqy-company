package com.weihua.dydz.repository;

import com.weihua.dydz.domain.WorkerRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkerRoleRepository extends JpaRepository<WorkerRole, Long> {
    Optional<WorkerRole> findByCode(String code);
}
