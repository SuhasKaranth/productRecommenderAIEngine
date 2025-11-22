package com.smartguide.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Intent extraction result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detected intent information")
public class IntentData {

    @Schema(description = "Detected user intent", example = "TRAVEL")
    private String detectedIntent;

    @Schema(description = "Confidence score (0.0 to 1.0)", example = "0.96")
    private Double confidence;

    @Schema(description = "Extracted entities from user input")
    private Map<String, Object> entities = new HashMap<>();
}
