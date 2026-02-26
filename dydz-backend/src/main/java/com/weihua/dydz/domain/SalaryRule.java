package com.weihua.dydz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "salary_rules")
public class SalaryRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private WorkerRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryRuleType ruleType;

    @Column(precision = 12, scale = 2)
    private BigDecimal pricePerTon;

    @Column(precision = 12, scale = 2)
    private BigDecimal dailySalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal hourlySalary;
}
