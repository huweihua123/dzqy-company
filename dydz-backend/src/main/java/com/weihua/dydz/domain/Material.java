package com.weihua.dydz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal multiplier = BigDecimal.ONE;

    @Column(length = 300)
    private String remark;
}
