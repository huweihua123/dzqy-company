package com.weihua.dydz.repository;

import com.weihua.dydz.domain.SalaryRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryRuleRepository extends JpaRepository<SalaryRule, Long> {
    Optional<SalaryRule> findByRoleId(Long roleId);
}
