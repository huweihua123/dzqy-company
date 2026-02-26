package com.weihua.dydz.repository;

import com.weihua.dydz.domain.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    boolean existsByName(String name);
    Owner findByName(String name);
}
