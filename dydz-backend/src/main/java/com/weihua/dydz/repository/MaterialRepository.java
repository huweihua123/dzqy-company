package com.weihua.dydz.repository;

import com.weihua.dydz.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    boolean existsByName(String name);
    Material findByName(String name);
}
