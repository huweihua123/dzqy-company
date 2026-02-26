package com.weihua.dydz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String orderNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal planGrossTonnage;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal planNetTonnage;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal planPieceCount;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal totalFee;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal doneGrossTonnage = BigDecimal.ZERO;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal doneNetTonnage = BigDecimal.ZERO;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal donePieceCount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductionStatus productionStatus = ProductionStatus.NOT_STARTED;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal receivableAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal outstandingAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
