package com.weihua.dydz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "production_records")
public class ProductionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @Column(nullable = false)
    private LocalDate workDate;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal grossTonnage;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal netTonnage;

    @Column(precision = 12, scale = 3)
    private BigDecimal pieceCount;
}
