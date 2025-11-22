package com.smartguide.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Individual product recommendation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Product recommendation details")
public class ProductRecommendation {

    @Schema(description = "Recommendation rank", example = "1")
    private Integer rank;

    @Schema(description = "Product ID", example = "1")
    private Long productId;

    @Schema(description = "Product code", example = "CC_TRAVEL_01")
    private String productCode;

    @Schema(description = "Product name", example = "Voyager Travel Credit Card")
    private String productName;

    @Schema(description = "Product category", example = "CREDIT_CARD")
    private String category;

    @Schema(description = "Islamic structure", example = "Murabaha")
    private String islamicStructure;

    @Schema(description = "Relevance score (0.0 to 1.0)", example = "0.92")
    private Double relevanceScore;

    @Schema(description = "Reason for recommendation")
    private String reason;

    @Schema(description = "Key benefits of the product")
    private List<String> keyBenefits;

    @Schema(description = "Annual fee", example = "150.0")
    private BigDecimal annualFee;

    @Schema(description = "Minimum income requirement", example = "50000.0")
    private BigDecimal minIncome;
}
