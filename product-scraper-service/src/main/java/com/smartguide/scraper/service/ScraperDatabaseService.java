package com.smartguide.scraper.service;

import com.smartguide.scraper.model.ScrapedProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database service for scraper operations
 * Note: This service uses JDBC directly to avoid entity duplication
 * In production, consider using a shared entity module
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScraperDatabaseService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Initialize a new scrape log entry
     */
    @Transactional
    public void initializeScrapeLog(String jobId, String websiteId) {
        String sql = """
            INSERT INTO scrape_logs (job_id, source_id, status, started_at)
            SELECT ?, id, 'RUNNING', NOW()
            FROM scrape_sources
            WHERE website_id = ?
            """;
        jdbcTemplate.update(sql, jobId, websiteId);
        log.info("Initialized scrape log for job: {}", jobId);
    }

    /**
     * Complete scrape log with results
     */
    @Transactional
    public void completeScrapeLog(String jobId, int productsFound, int productsSaved, String errorMessage) {
        String status = errorMessage != null ? "FAILED" : "SUCCESS";
        String sql = """
            UPDATE scrape_logs
            SET status = ?, products_found = ?, products_saved = ?,
                products_updated = 0, products_skipped = ?,
                error_message = ?, completed_at = NOW()
            WHERE job_id = ?
            """;
        jdbcTemplate.update(sql, status, productsFound, productsSaved,
                productsFound - productsSaved, errorMessage, jobId);
        log.info("Completed scrape log for job: {}", jobId);
    }

    /**
     * Save scraped products to staging table for review
     */
    @Transactional
    public int saveProducts(List<ScrapedProduct> products, LocalDateTime scrapedAt, String jobId) {
        int savedCount = 0;

        // Get scrape_log_id from job_id
        Long scrapeLogId = getScrapeLogIdByJobId(jobId);

        String insertSql = """
            INSERT INTO staging_products (
                product_code, product_name, category, sub_category, description,
                islamic_structure, annual_rate, annual_fee, min_income, min_credit_score,
                eligibility_criteria, key_benefits, sharia_certified, active,
                source_website_id, source_url, scraped_at, data_quality_score,
                scrape_log_id, approval_status, ai_suggested_category, ai_confidence,
                ai_categorization_json, raw_html, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?, ?, ?::jsonb, ?, NOW())
            """;

        for (ScrapedProduct product : products) {
            try {
                // Generate product code if missing
                String productCode = product.getProductCode() != null
                        ? product.getProductCode()
                        : generateProductCode(product);

                // Convert lists/maps to JSON strings
                String keyBenefitsJson = product.getKeyBenefits() != null
                        ? convertToJson(product.getKeyBenefits())
                        : "[]";

                String eligibilityJson = product.getEligibilityCriteria() != null
                        ? convertToJson(product.getEligibilityCriteria())
                        : "{}";

                String aiCategorizationJson = product.getAiCategorizationJson() != null
                        ? convertToJson(product.getAiCategorizationJson())
                        : "{}";

                jdbcTemplate.update(insertSql,
                        productCode,
                        product.getProductName(),
                        product.getCategory(),
                        product.getSubCategory(),
                        product.getDescription(),
                        product.getIslamicStructure(),
                        product.getAnnualRate(),
                        product.getAnnualFee(),
                        product.getMinIncome(),
                        product.getMinCreditScore(),
                        eligibilityJson,
                        keyBenefitsJson,
                        product.getShariaCertified(),
                        product.getActive(),
                        product.getSourceWebsiteId(),
                        product.getSourceUrl(),
                        scrapedAt,
                        product.getDataQualityScore(),
                        scrapeLogId,
                        product.getAiSuggestedCategory(),
                        product.getAiConfidence(),
                        aiCategorizationJson,
                        product.getRawHtml()
                );

                savedCount++;
            } catch (Exception e) {
                log.error("Failed to save product: {}", product.getProductName(), e);
            }
        }

        return savedCount;
    }

    /**
     * Update last scraped time for a website
     */
    @Transactional
    public void updateLastScrapedTime(String websiteId, LocalDateTime timestamp) {
        String sql = "UPDATE scrape_sources SET last_scraped_at = ? WHERE website_id = ?";
        jdbcTemplate.update(sql, timestamp, websiteId);
    }

    /**
     * Get scrape log by job ID
     */
    public Map<String, Object> getScrapeLog(String jobId) {
        String sql = """
            SELECT sl.*, ss.website_id, ss.website_name
            FROM scrape_logs sl
            JOIN scrape_sources ss ON sl.source_id = ss.id
            WHERE sl.job_id = ?
            """;
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, jobId);
        return results.isEmpty() ? new HashMap<>() : results.get(0);
    }

    /**
     * Get all scrape sources
     */
    public List<Map<String, Object>> getAllScrapeSources() {
        String sql = "SELECT * FROM scrape_sources ORDER BY website_name";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Get scrape history for a website
     */
    public List<Map<String, Object>> getScrapeHistory(String websiteId) {
        String sql = """
            SELECT sl.*
            FROM scrape_logs sl
            JOIN scrape_sources ss ON sl.source_id = ss.id
            WHERE ss.website_id = ?
            ORDER BY sl.started_at DESC
            LIMIT 50
            """;
        return jdbcTemplate.queryForList(sql, websiteId);
    }

    /**
     * Get scrape_log_id from job_id
     */
    private Long getScrapeLogIdByJobId(String jobId) {
        String sql = "SELECT id FROM scrape_logs WHERE job_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, jobId);
        } catch (Exception e) {
            log.error("Failed to get scrape_log_id for job: {}", jobId, e);
            return null;
        }
    }

    /**
     * Generate product code from product data
     */
    private String generateProductCode(ScrapedProduct product) {
        String prefix = product.getSourceWebsiteId() != null
                ? product.getSourceWebsiteId().toUpperCase()
                : "UNK";
        String name = product.getProductName() != null
                ? product.getProductName().replaceAll("[^A-Za-z0-9]", "").toUpperCase()
                : "PRODUCT";
        String timestamp = String.valueOf(System.currentTimeMillis());

        return prefix + "_" + name.substring(0, Math.min(10, name.length())) + "_" + timestamp.substring(timestamp.length() - 6);
    }

    /**
     * Convert object to JSON string
     */
    private String convertToJson(Object obj) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Failed to convert to JSON", e);
            return obj instanceof List ? "[]" : "{}";
        }
    }
}
