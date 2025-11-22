package com.smartguide.scraper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartguide.scraper.model.ScrapedProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to enrich scraped product data using LLM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LLMDataEnricher {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${app.main-service.url:http://localhost:8080}")
    private String mainServiceUrl;

    /**
     * Enrich product data using LLM to parse unstructured content
     */
    public ScrapedProduct enrichProduct(ScrapedProduct product) {
        if (product.getRawHtml() == null || product.getRawHtml().isEmpty()) {
            log.warn("No raw HTML available for enrichment");
            return product;
        }

        try {
            String prompt = buildEnrichmentPrompt(product);
            String llmResponse = callLLMService(prompt);

            // Parse LLM response and update product
            updateProductFromLLMResponse(product, llmResponse);

            log.info("Successfully enriched product: {}", product.getProductName());
        } catch (Exception e) {
            log.error("Failed to enrich product with LLM", e);
        }

        return product;
    }

    /**
     * Calculate data quality score for scraped product
     */
    public BigDecimal calculateQualityScore(ScrapedProduct product) {
        int totalFields = 14;
        int filledFields = 0;

        if (product.getProductName() != null && !product.getProductName().isEmpty()) filledFields++;
        if (product.getProductCode() != null && !product.getProductCode().isEmpty()) filledFields++;
        if (product.getCategory() != null && !product.getCategory().isEmpty()) filledFields++;
        if (product.getSubCategory() != null && !product.getSubCategory().isEmpty()) filledFields++;
        if (product.getDescription() != null && !product.getDescription().isEmpty()) filledFields++;
        if (product.getIslamicStructure() != null && !product.getIslamicStructure().isEmpty()) filledFields++;
        if (product.getAnnualRate() != null) filledFields++;
        if (product.getAnnualFee() != null) filledFields++;
        if (product.getMinIncome() != null) filledFields++;
        if (product.getMinCreditScore() != null) filledFields++;
        if (product.getEligibilityCriteria() != null && !product.getEligibilityCriteria().isEmpty()) filledFields++;
        if (product.getKeyBenefits() != null && !product.getKeyBenefits().isEmpty()) filledFields++;
        if (product.getShariaCertified() != null) filledFields++;
        if (product.getActive() != null) filledFields++;

        double score = (double) filledFields / totalFields;
        return BigDecimal.valueOf(score).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Build prompt for LLM enrichment
     */
    private String buildEnrichmentPrompt(ScrapedProduct product) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Extract and structure banking product information from the following HTML content.\n\n");
        prompt.append("Product URL: ").append(product.getSourceUrl()).append("\n\n");
        prompt.append("Please extract the following fields in JSON format:\n");
        prompt.append("- productName: Full name of the product\n");
        prompt.append("- productCode: Product code or identifier\n");
        prompt.append("- category: Product category (e.g., CREDIT_CARD, FINANCING, SAVINGS, etc.)\n");
        prompt.append("- subCategory: Sub-category if applicable\n");
        prompt.append("- description: Brief description of the product\n");
        prompt.append("- islamicStructure: Islamic finance structure (e.g., Murabaha, Tawarruq, Musharaka)\n");
        prompt.append("- annualRate: Annual rate/profit rate as a number\n");
        prompt.append("- annualFee: Annual fee as a number\n");
        prompt.append("- minIncome: Minimum income requirement as a number\n");
        prompt.append("- minCreditScore: Minimum credit score as a number\n");
        prompt.append("- keyBenefits: List of key benefits/features\n");
        prompt.append("- shariaCertified: true if Sharia-compliant\n\n");
        prompt.append("HTML Content (truncated to first 2000 chars):\n");
        prompt.append(product.getRawHtml().substring(0, Math.min(2000, product.getRawHtml().length())));

        return prompt.toString();
    }

    /**
     * Call the main service LLM API
     */
    private String callLLMService(String prompt) {
        WebClient webClient = webClientBuilder.baseUrl(mainServiceUrl).build();

        Map<String, Object> request = new HashMap<>();
        request.put("userQuery", prompt);
        request.put("userContext", Map.of());

        try {
            String response = webClient.post()
                    .uri("/api/recommendations")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response;
        } catch (Exception e) {
            log.error("Failed to call LLM service", e);
            throw new RuntimeException("LLM service call failed", e);
        }
    }

    /**
     * Update product fields from LLM response
     */
    private void updateProductFromLLMResponse(ScrapedProduct product, String llmResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(llmResponse);

            if (jsonNode.has("productName") && product.getProductName() == null) {
                product.setProductName(jsonNode.get("productName").asText());
            }
            if (jsonNode.has("productCode") && product.getProductCode() == null) {
                product.setProductCode(jsonNode.get("productCode").asText());
            }
            if (jsonNode.has("category") && product.getCategory() == null) {
                product.setCategory(jsonNode.get("category").asText());
            }
            if (jsonNode.has("description") && product.getDescription() == null) {
                product.setDescription(jsonNode.get("description").asText());
            }
            if (jsonNode.has("islamicStructure") && product.getIslamicStructure() == null) {
                product.setIslamicStructure(jsonNode.get("islamicStructure").asText());
            }
            if (jsonNode.has("keyBenefits") && (product.getKeyBenefits() == null || product.getKeyBenefits().isEmpty())) {
                List<String> benefits = objectMapper.convertValue(
                        jsonNode.get("keyBenefits"),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                );
                product.setKeyBenefits(benefits);
            }

            // Calculate and set quality score
            product.setDataQualityScore(calculateQualityScore(product));

        } catch (Exception e) {
            log.error("Failed to parse LLM response", e);
        }
    }
}
