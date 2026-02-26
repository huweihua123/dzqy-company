package com.weihua.dydz.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_items")
@JsonIgnoreProperties({"order"})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "spec_id")
    private Spec spec;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal pieceCount;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal pieceWeight;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal grossTonnage;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal netTonnage;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal donePieceCount = BigDecimal.ZERO;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal doneGrossTonnage = BigDecimal.ZERO;

    @Column(precision = 12, scale = 3, nullable = false)
    private BigDecimal doneNetTonnage = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;
}
