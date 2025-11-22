package com.smartguide.poc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * ScrapeLog Entity - represents historical scraping jobs
 */
@Entity
@Table(name = "scrape_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrapeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private ScrapeSource scrapeSource;

    @Column(name = "job_id", unique = true, nullable = false, length = 100)
    private String jobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ScrapeStatus status;

    @Column(name = "products_found")
    private Integer productsFound = 0;

    @Column(name = "products_saved")
    private Integer productsSaved = 0;

    @Column(name = "products_updated")
    private Integer productsUpdated = 0;

    @Column(name = "products_skipped")
    private Integer productsSkipped = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public enum ScrapeStatus {
        RUNNING,
        SUCCESS,
        FAILED,
        PARTIAL
    }
}
