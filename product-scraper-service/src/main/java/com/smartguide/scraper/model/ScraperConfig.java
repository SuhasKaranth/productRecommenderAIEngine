package com.smartguide.scraper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Model representing a website scraper configuration loaded from YAML
 */
@Data
@NoArgsConstructor
public class ScraperConfig {

    private String websiteId;
    private String websiteName;
    private String baseUrl;
    private NavigationConfig navigation;
    private SelectorConfig selectors;
    private MappingConfig mapping;
    private ScraperOptions options;

    @Data
    @NoArgsConstructor
    public static class NavigationConfig {
        private String startUrl;
        private String productListUrl;
        private Integer maxPages;
        private String nextPageSelector;
        private Integer waitAfterLoad; // milliseconds
    }

    @Data
    @NoArgsConstructor
    public static class SelectorConfig {
        private String productList;
        private String productLink;
        private String productName;
        private String productCode;
        private String category;
        private String subCategory;
        private String description;
        private String annualRate;
        private String annualFee;
        private String minIncome;
        private String minCreditScore;
        private String keyBenefits;
        private String eligibilityCriteria;
        private String islamicStructure;
    }

    @Data
    @NoArgsConstructor
    public static class MappingConfig {
        private String defaultCategory;
        private Map<String, String> categoryMapping;
        private Boolean shariaCertified;
        private Boolean active;
    }

    @Data
    @NoArgsConstructor
    public static class ScraperOptions {
        private Boolean aiEnrichment;
        private Boolean headless;
        private Integer timeout; // milliseconds
        private Boolean screenshot;
        private String screenshotPath;
        private Integer retryCount;
        private Integer delayBetweenRequests; // milliseconds
    }
}
