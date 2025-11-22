package com.smartguide.scraper.service;

import com.microsoft.playwright.*;
import com.smartguide.scraper.model.ScraperConfig;
import com.smartguide.scraper.model.ScrapedProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Playwright-based web scraping engine
 */
@Service
@Slf4j
public class PlaywrightScraperEngine {

    /**
     * Scrape products from a website based on configuration
     */
    public List<ScrapedProduct> scrapeWebsite(ScraperConfig config) {
        List<ScrapedProduct> scrapedProducts = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(config.getOptions().getHeadless() != null
                            ? config.getOptions().getHeadless()
                            : true);

            try (Browser browser = playwright.chromium().launch(launchOptions)) {
                BrowserContext context = browser.newContext();
                Page page = context.newPage();

                // Set timeout
                int timeout = config.getOptions().getTimeout() != null
                        ? config.getOptions().getTimeout()
                        : 30000;
                page.setDefaultTimeout(timeout);

                log.info("Navigating to: {}", config.getNavigation().getStartUrl());
                page.navigate(config.getNavigation().getStartUrl());

                // Wait after page load if specified
                if (config.getNavigation().getWaitAfterLoad() != null) {
                    page.waitForTimeout(config.getNavigation().getWaitAfterLoad());
                }

                // Get product links
                List<String> productUrls = extractProductUrls(page, config);
                log.info("Found {} product URLs", productUrls.size());

                // Scrape each product
                int count = 0;
                for (String productUrl : productUrls) {
                    try {
                        ScrapedProduct product = scrapeProductPage(page, productUrl, config);
                        if (product != null) {
                            scrapedProducts.add(product);
                            count++;
                        }

                        // Delay between requests
                        if (config.getOptions().getDelayBetweenRequests() != null) {
                            page.waitForTimeout(config.getOptions().getDelayBetweenRequests());
                        }
                    } catch (Exception e) {
                        log.error("Failed to scrape product: {}", productUrl, e);
                    }
                }

                log.info("Successfully scraped {} products", count);
            }
        } catch (Exception e) {
            log.error("Scraping failed for website: {}", config.getWebsiteId(), e);
        }

        return scrapedProducts;
    }

    /**
     * Extract product URLs from listing page
     */
    private List<String> extractProductUrls(Page page, ScraperConfig config) {
        List<String> urls = new ArrayList<>();

        try {
            Locator productElements = page.locator(config.getSelectors().getProductList());
            int count = productElements.count();

            for (int i = 0; i < count; i++) {
                try {
                    Locator linkElement = productElements.nth(i).locator(config.getSelectors().getProductLink());
                    String href = linkElement.getAttribute("href");

                    if (href != null) {
                        // Handle relative URLs
                        if (!href.startsWith("http")) {
                            href = config.getBaseUrl() + (href.startsWith("/") ? "" : "/") + href;
                        }
                        urls.add(href);
                    }
                } catch (Exception e) {
                    log.warn("Failed to extract product URL at index {}", i);
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract product URLs", e);
        }

        return urls;
    }

    /**
     * Scrape a single product page
     */
    private ScrapedProduct scrapeProductPage(Page page, String url, ScraperConfig config) {
        try {
            log.debug("Scraping product: {}", url);
            page.navigate(url);

            if (config.getNavigation().getWaitAfterLoad() != null) {
                page.waitForTimeout(config.getNavigation().getWaitAfterLoad());
            }

            ScrapedProduct.ScrapedProductBuilder builder = ScrapedProduct.builder()
                    .sourceWebsiteId(config.getWebsiteId())
                    .sourceUrl(url);

            // Extract product data using selectors
            builder.productName(extractText(page, config.getSelectors().getProductName()));
            builder.productCode(extractText(page, config.getSelectors().getProductCode()));
            builder.category(extractText(page, config.getSelectors().getCategory()));
            builder.subCategory(extractText(page, config.getSelectors().getSubCategory()));
            builder.description(extractText(page, config.getSelectors().getDescription()));
            builder.islamicStructure(extractText(page, config.getSelectors().getIslamicStructure()));

            // Extract numeric fields
            builder.annualRate(extractDecimal(page, config.getSelectors().getAnnualRate()));
            builder.annualFee(extractDecimal(page, config.getSelectors().getAnnualFee()));
            builder.minIncome(extractDecimal(page, config.getSelectors().getMinIncome()));
            builder.minCreditScore(extractInteger(page, config.getSelectors().getMinCreditScore()));

            // Extract list fields
            builder.keyBenefits(extractList(page, config.getSelectors().getKeyBenefits()));

            // Apply mapping config defaults
            if (config.getMapping() != null) {
                if (builder.build().getCategory() == null && config.getMapping().getDefaultCategory() != null) {
                    builder.category(config.getMapping().getDefaultCategory());
                }
                builder.shariaCertified(config.getMapping().getShariaCertified() != null
                        ? config.getMapping().getShariaCertified()
                        : true);
                builder.active(config.getMapping().getActive() != null
                        ? config.getMapping().getActive()
                        : true);
            }

            // Store raw HTML for AI enrichment if enabled
            if (config.getOptions().getAiEnrichment() != null && config.getOptions().getAiEnrichment()) {
                builder.rawHtml(page.content());
            }

            return builder.build();
        } catch (Exception e) {
            log.error("Failed to scrape product page: {}", url, e);
            return null;
        }
    }

    private String extractText(Page page, String selector) {
        if (selector == null || selector.isEmpty()) return null;
        try {
            return page.locator(selector).first().textContent();
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal extractDecimal(Page page, String selector) {
        String text = extractText(page, selector);
        if (text == null) return null;
        try {
            // Remove non-numeric characters except decimal point
            String cleaned = text.replaceAll("[^0-9.]", "");
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer extractInteger(Page page, String selector) {
        String text = extractText(page, selector);
        if (text == null) return null;
        try {
            String cleaned = text.replaceAll("[^0-9]", "");
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> extractList(Page page, String selector) {
        if (selector == null || selector.isEmpty()) return new ArrayList<>();
        try {
            List<String> items = new ArrayList<>();
            Locator elements = page.locator(selector);
            int count = elements.count();
            for (int i = 0; i < count; i++) {
                String text = elements.nth(i).textContent();
                if (text != null && !text.trim().isEmpty()) {
                    items.add(text.trim());
                }
            }
            return items;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
