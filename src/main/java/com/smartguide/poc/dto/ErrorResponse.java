package com.smartguide.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Error response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Error response")
public class ErrorResponse {

    @Schema(description = "Response status", example = "error")
    @Builder.Default
    private String status = "error";

    @Schema(description = "Error code", example = "PROCESSING_ERROR")
    private String errorCode;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Additional error details")
    private Map<String, Object> details;
}
