package com.weihua.dydz.repository;

import com.weihua.dydz.domain.Spec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecRepository extends JpaRepository<Spec, Long> {
    boolean existsByName(String name);
    Spec findByName(String name);
}
