package com.weihua.dydz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "workers")
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private WorkerRole role;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private WorkerGroup group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayType payType;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkerStatus status = WorkerStatus.ACTIVE;

    @Column(precision = 12, scale = 2)
    private BigDecimal tonPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal dailySalary;

    @Column(precision = 12, scale = 2)
    private BigDecimal yearlySalary;

    @Column(length = 300)
    private String remark;
}
