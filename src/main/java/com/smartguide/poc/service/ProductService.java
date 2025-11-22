package com.smartguide.poc.service;

import com.smartguide.poc.entity.Product;
import com.smartguide.poc.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for product retrieval and ranking
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    /**
     * Get product recommendations based on filters and intent
     */
    public List<Map<String, Object>> getRecommendations(
            Map<String, Object> filters,
            Map<String, Object> intentData,
            Map<String, Object> categories) {

        List<Product> products = queryProducts(filters);

        if (products.isEmpty()) {
            log.warn("No products found with filters, getting fallback products");
            products = getFallbackProducts();
        }

        // Add categories to intent data for ranking
        Map<String, Object> enrichedIntentData = new HashMap<>(intentData);
        enrichedIntentData.put("primary_category", categories.get("primary"));
        enrichedIntentData.put("secondary_categories", categories.get("secondary"));

        List<Map<String, Object>> rankedProducts = rankProducts(products, enrichedIntentData);

        // Return top 5
        return rankedProducts.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * Query products from database with filters
     */
    @SuppressWarnings("unchecked")
    private List<Product> queryProducts(Map<String, Object> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // Category filter
        List<String> categories = (List<String>) filters.get("categories");
        if (categories != null && !categories.isEmpty()) {
            predicates.add(product.get("category").in(categories));
        }

        // Active filter
        if (filters.containsKey("active")) {
            predicates.add(cb.equal(product.get("active"), filters.get("active")));
        }

        // Sharia certified filter
        if (filters.containsKey("sharia_certified")) {
            predicates.add(cb.equal(product.get("shariaCertified"), filters.get("sharia_certified")));
        }

        // User income filter
        if (filters.containsKey("user_income")) {
            BigDecimal userIncome = (BigDecimal) filters.get("user_income");
            predicates.add(cb.or(
                    cb.lessThanOrEqualTo(product.get("minIncome"), userIncome),
                    cb.isNull(product.get("minIncome"))
            ));
        }

        // Credit score filter
        if (filters.containsKey("user_credit_score")) {
            Integer creditScore = (Integer) filters.get("user_credit_score");
            predicates.add(cb.or(
                    cb.lessThanOrEqualTo(product.get("minCreditScore"), creditScore),
                    cb.isNull(product.get("minCreditScore"))
            ));
        }

        // Exclude products filter
        if (filters.containsKey("exclude_products")) {
            List<String> excludeProducts = (List<String>) filters.get("exclude_products");
            if (!excludeProducts.isEmpty()) {
                predicates.add(cb.not(product.get("productCode").in(excludeProducts)));
            }
        }

        query.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Get generic fallback products when no specific matches
     */
    private List<Product> getFallbackProducts() {
        List<String> fallbackCategories = Arrays.asList("CASA", "CREDIT_CARD", "INVESTMENT");

        return productRepository.findByCategoriesWithBasicFilters(fallbackCategories)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Rank products based on relevance to intent
     */
    private List<Map<String, Object>> rankProducts(List<Product> products, Map<String, Object> intentData) {
        List<Map<String, Object>> scoredProducts = new ArrayList<>();

        for (Product product : products) {
            double score = calculateRelevanceScore(product, intentData);
            String reason = generateReason(product, intentData);

            Map<String, Object> scoredProduct = new HashMap<>();
            scoredProduct.put("product", product);
            scoredProduct.put("score", score);
            scoredProduct.put("reason", reason);

            scoredProducts.add(scoredProduct);
        }

        // Sort by score descending
        scoredProducts.sort((a, b) ->
                Double.compare((Double) b.get("score"), (Double) a.get("score"))
        );

        return scoredProducts;
    }

    /**
     * Calculate relevance score for a product
     * Ranking formula:
     * - Category match: 50%
     * - Recency: 20%
     * - Popularity: 15%
     * - Benefit alignment: 15%
     */
    @SuppressWarnings("unchecked")
    private double calculateRelevanceScore(Product product, Map<String, Object> intentData) {
        double score = 0.0;

        String primaryCategory = (String) intentData.get("primary_category");
        List<String> secondaryCategories = (List<String>) intentData.get("secondary_categories");

        // 1. Category match (50%)
        if (product.getCategory().equals(primaryCategory)) {
            score += 0.50;
        } else if (secondaryCategories != null && secondaryCategories.contains(product.getCategory())) {
            score += 0.35;
        } else {
            score += 0.10;
        }

        // 2. Recency (20%)
        if (product.getCreatedAt() != null) {
            long daysOld = ChronoUnit.DAYS.between(product.getCreatedAt(), LocalDateTime.now());
            if (daysOld < 30) {
                score += 0.20;
            } else if (daysOld < 90) {
                score += 0.15;
            } else {
                score += 0.10;
            }
        } else {
            score += 0.10;
        }

        // 3. Popularity (15%) - Simplified for POC
        List<String> popularProducts = Arrays.asList("CC_TRAVEL_01", "CASA_SAV_01", "FIN_HOME_01");
        if (popularProducts.contains(product.getProductCode())) {
            score += 0.15;
        } else {
            score += 0.08;
        }

        // 4. Benefit alignment (15%)
        String intent = (String) intentData.get("intent");
        if (checkBenefitAlignment(product, intent)) {
            score += 0.15;
        } else {
            score += 0.05;
        }

        return Math.min(1.0, Math.max(0.0, score));
    }

    /**
     * Check if product benefits align with intent
     */
    private boolean checkBenefitAlignment(Product product, String intent) {
        if (product.getKeyBenefits() == null || product.getKeyBenefits().isEmpty()) {
            return false;
        }

        String benefitsText = String.join(" ", product.getKeyBenefits()).toLowerCase();

        Map<String, List<String>> intentKeywords = Map.of(
                "TRAVEL", Arrays.asList("travel", "forex", "international", "airport", "lounge"),
                "LOAN", Arrays.asList("finance", "loan", "credit", "tenure", "payment"),
                "SAVINGS", Arrays.asList("profit", "return", "savings", "monthly"),
                "INVESTMENT", Arrays.asList("fund", "portfolio", "dividend", "growth"),
                "CAR", Arrays.asList("auto", "vehicle", "car", "motor"),
                "HOME", Arrays.asList("home", "house", "property", "mortgage"),
                "EDUCATION", Arrays.asList("education", "study", "university", "tuition"),
                "BUSINESS", Arrays.asList("business", "sme", "corporate", "company"),
                "INSURANCE", Arrays.asList("coverage", "protection", "takaful", "claim"),
                "PAYMENT", Arrays.asList("payment", "cashback", "rewards", "card")
        );

        List<String> keywords = intentKeywords.getOrDefault(intent, Collections.emptyList());
        return keywords.stream().anyMatch(benefitsText::contains);
    }

    /**
     * Generate explanation for why product was recommended
     */
    private String generateReason(Product product, Map<String, Object> intentData) {
        String intent = (String) intentData.get("intent");
        String structure = product.getIslamicStructure() != null
                ? product.getIslamicStructure()
                : "Sharia-compliant";

        Map<String, String> reasonTemplates = Map.of(
                "TRAVEL", "Perfect for travelers with %s structure and travel benefits",
                "LOAN", "Flexible %s financing to meet your needs",
                "SAVINGS", "Grow your wealth with %s profit-sharing",
                "INVESTMENT", "Build your portfolio with %s investment",
                "CAR", "Drive your dream car with %s auto financing",
                "HOME", "Own your home through %s partnership",
                "EDUCATION", "Invest in education with %s financing",
                "BUSINESS", "Grow your business with %s solutions",
                "INSURANCE", "Comprehensive protection through %s",
                "PAYMENT", "Convenient payments with %s structure"
        );

        String baseReason = String.format(
                reasonTemplates.getOrDefault(intent,
                        "Sharia-compliant " + product.getCategory().replace("_", " ").toLowerCase()),
                structure
        );

        Double confidence = (Double) intentData.get("confidence");
        if (confidence != null && confidence > 0.8) {
            baseReason += " - Highly relevant to your needs";
        }

        return baseReason;
    }
}
