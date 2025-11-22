package com.smartguide.poc.admin.dto;

import com.smartguide.poc.entity.StagingProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StagingProductDTO {
    private Long id;
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
    private LocalDateTime scrapedAt;
    private BigDecimal dataQualityScore;

    // Staging metadata
    private Long scrapeLogId;
    private String approvalStatus;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewNotes;

    // AI categorization
    private String aiSuggestedCategory;
    private BigDecimal aiConfidence;
    private Map<String, Object> aiCategorizationJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StagingProductDTO fromEntity(StagingProduct entity) {
        return StagingProductDTO.builder()
                .id(entity.getId())
                .productCode(entity.getProductCode())
                .productName(entity.getProductName())
                .category(entity.getCategory())
                .subCategory(entity.getSubCategory())
                .description(entity.getDescription())
                .islamicStructure(entity.getIslamicStructure())
                .annualRate(entity.getAnnualRate())
                .annualFee(entity.getAnnualFee())
                .minIncome(entity.getMinIncome())
                .minCreditScore(entity.getMinCreditScore())
                .eligibilityCriteria(entity.getEligibilityCriteria())
                .keyBenefits(entity.getKeyBenefits())
                .shariaCertified(entity.getShariaCertified())
                .active(entity.getActive())
                .sourceWebsiteId(entity.getSourceWebsiteId())
                .sourceUrl(entity.getSourceUrl())
                .scrapedAt(entity.getScrapedAt())
                .dataQualityScore(entity.getDataQualityScore())
                .scrapeLogId(entity.getScrapeLog() != null ? entity.getScrapeLog().getId() : null)
                .approvalStatus(entity.getApprovalStatus() != null ? entity.getApprovalStatus().name() : null)
                .reviewedBy(entity.getReviewedBy())
                .reviewedAt(entity.getReviewedAt())
                .reviewNotes(entity.getReviewNotes())
                .aiSuggestedCategory(entity.getAiSuggestedCategory())
                .aiConfidence(entity.getAiConfidence())
                .aiCategorizationJson(entity.getAiCategorizationJson())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
