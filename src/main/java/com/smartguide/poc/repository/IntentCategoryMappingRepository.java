package com.smartguide.poc.repository;

import com.smartguide.poc.entity.IntentCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for IntentCategoryMapping entity
 */
@Repository
public interface IntentCategoryMappingRepository extends JpaRepository<IntentCategoryMapping, Long> {

    /**
     * Find mapping by intent
     */
    Optional<IntentCategoryMapping> findByIntent(String intent);

    /**
     * Find mapping by intent (case-insensitive)
     */
    Optional<IntentCategoryMapping> findByIntentIgnoreCase(String intent);
}
