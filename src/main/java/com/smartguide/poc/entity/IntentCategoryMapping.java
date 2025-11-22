package com.smartguide.poc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Intent Category Mapping Entity - maps user intents to product categories
 */
@Entity
@Table(name = "intent_category_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentCategoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intent", unique = true, nullable = false, length = 100)
    private String intent;

    @Column(name = "primary_category", nullable = false, length = 100)
    private String primaryCategory;

    @ElementCollection
    @CollectionTable(
            name = "intent_secondary_categories",
            joinColumns = @JoinColumn(name = "intent_mapping_id")
    )
    @Column(name = "category")
    private List<String> secondaryCategories;

    @Column(name = "confidence_threshold", precision = 3, scale = 2)
    private BigDecimal confidenceThreshold = new BigDecimal("0.75");

    @Column(name = "rank_order")
    private Integer rankOrder = 1;
}
