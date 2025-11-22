package com.smartguide.poc.service;

import com.smartguide.poc.dto.UserContext;
import com.smartguide.poc.entity.IntentCategoryMapping;
import com.smartguide.poc.repository.IntentCategoryMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rules Engine for mapping intents to product categories
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RulesEngine {

    private final IntentCategoryMappingRepository intentMappingRepository;

    /**
     * Map intent to product categories based on rules
     */
    public Map<String, Object> getProductCategories(String intent, Double confidence) {
        Optional<IntentCategoryMapping> mappingOpt = intentMappingRepository.findByIntent(intent);

        if (mappingOpt.isEmpty()) {
            log.warn("No mapping found for intent: {}, using defaults", intent);
            return getDefaultCategories();
        }

        IntentCategoryMapping mapping = mappingOpt.get();
        double confidenceThreshold = mapping.getConfidenceThreshold() != null
                ? mapping.getConfidenceThreshold().doubleValue()
                : 0.75;

        Map<String, Object> result = new HashMap<>();

        if (confidence < confidenceThreshold) {
            log.info("Low confidence {} for intent {}, including broader categories", confidence, intent);

            List<String> secondary = new ArrayList<>(
                    mapping.getSecondaryCategories() != null
                            ? mapping.getSecondaryCategories()
                            : Collections.emptyList()
            );
            secondary.add("CASA");
            secondary.add("CREDIT_CARD");

            // Remove duplicates
            secondary = secondary.stream().distinct().collect(Collectors.toList());

            result.put("primary", mapping.getPrimaryCategory());
            result.put("secondary", secondary);
            result.put("confidence_adjusted", true);
        } else {
            result.put("primary", mapping.getPrimaryCategory());
            result.put("secondary", mapping.getSecondaryCategories() != null
                    ? mapping.getSecondaryCategories()
                    : Collections.emptyList());
            result.put("confidence_adjusted", false);
        }

        return result;
    }

    /**
     * Build filters for product query based on categories and user context
     */
    public Map<String, Object> buildProductFilters(Map<String, Object> categories, UserContext userContext) {
        List<String> allCategories = new ArrayList<>();
        allCategories.add((String) categories.get("primary"));

        @SuppressWarnings("unchecked")
        List<String> secondaryCategories = (List<String>) categories.get("secondary");
        if (secondaryCategories != null) {
            allCategories.addAll(secondaryCategories);
        }

        Map<String, Object> filters = new HashMap<>();
        filters.put("categories", allCategories);
        filters.put("active", true);
        filters.put("sharia_certified", true);

        if (userContext != null) {
            if (userContext.getMinIncome() != null) {
                filters.put("user_income", userContext.getMinIncome());
            }

            if (userContext.getCreditScore() != null) {
                filters.put("user_credit_score", userContext.getCreditScore());
            }

            if (userContext.getCurrentProducts() != null && !userContext.getCurrentProducts().isEmpty()) {
                filters.put("exclude_products", userContext.getCurrentProducts());
            }

            if (userContext.getAge() != null) {
                filters.put("user_age", userContext.getAge());
            }
        }

        return filters;
    }

    /**
     * Get default categories when no mapping is found
     */
    private Map<String, Object> getDefaultCategories() {
        Map<String, Object> result = new HashMap<>();
        result.put("primary", "CASA");
        result.put("secondary", Arrays.asList("CREDIT_CARD", "INVESTMENT"));
        result.put("confidence_adjusted", true);
        return result;
    }
}
