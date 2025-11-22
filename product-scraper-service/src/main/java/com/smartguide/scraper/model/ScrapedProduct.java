package com.smartguide.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Model representing a scraped product before persistence
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedProduct {

    private String productCode;
    private String productName;
    private String category;
    private String subCategory;
    private String description;
    private String islamicStructure;
    private BigDecimal annualRate;
    private BigDecimal annualFee;
    private BigDecimal minIncome;
    private Integer minCreditScore;
    private Map<String, Object> eligibilityCriteria;
    private List<String> keyBenefits;
    private Boolean shariaCertified;
    private Boolean active;

    // Scraping metadata
    private String sourceWebsiteId;
    private String sourceUrl;
    private BigDecimal dataQualityScore;

    // Raw HTML content for AI enrichment
    private String rawHtml;

    // AI categorization fields
    private String aiSuggestedCategory;
    private BigDecimal aiConfidence;
    private Map<String, Object> aiCategorizationJson;
}
