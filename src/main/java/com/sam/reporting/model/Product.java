package com.sam.reporting.model;

import jakarta.persistence.*;           // Entity, Id, GeneratedValue, ManyToOne, etc.
import java.math.BigDecimal;           // money / prices

import lombok.*;

@Data
@ToString(exclude = "category")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include   // <‑‑ add this
    private Long id;

    private String name;

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;          // make sure Category is in the same package
}
