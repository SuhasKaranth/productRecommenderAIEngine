package com.smartguide.scraper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapeJobResponse {
    private String jobId;
    private String websiteId;
    private String status;
    private String message;
}
