package com.smartguide.poc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * ScrapeSource Entity - represents configured websites for scraping
 */
@Entity
@Table(name = "scrape_sources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrapeSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "website_id", unique = true, nullable = false, length = 100)
    private String websiteId;

    @Column(name = "website_name", nullable = false)
    private String websiteName;

    @Column(name = "base_url", nullable = false, columnDefinition = "TEXT")
    private String baseUrl;

    @Column(name = "config_path", length = 500)
    private String configPath;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "last_scraped_at")
    private LocalDateTime lastScrapedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
