package com.weihua.dydz.repository;

import com.weihua.dydz.domain.AccountLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountLogRepository extends JpaRepository<AccountLog, Long> {
    Optional<AccountLog> findTopByWorkerIdOrderByCreatedAtDesc(Long workerId);
}
