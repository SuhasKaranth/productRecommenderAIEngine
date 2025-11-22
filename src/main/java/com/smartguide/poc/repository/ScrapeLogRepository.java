package com.smartguide.poc.repository;

import com.smartguide.poc.entity.ScrapeLog;
import com.smartguide.poc.entity.ScrapeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ScrapeLog entity
 */
@Repository
public interface ScrapeLogRepository extends JpaRepository<ScrapeLog, Long> {

    Optional<ScrapeLog> findByJobId(String jobId);

    List<ScrapeLog> findByScrapeSourceOrderByStartedAtDesc(ScrapeSource scrapeSource);

    List<ScrapeLog> findByStatusOrderByStartedAtDesc(ScrapeLog.ScrapeStatus status);

    @Query("SELECT sl FROM ScrapeLog sl ORDER BY sl.startedAt DESC")
    List<ScrapeLog> findAllOrderByStartedAtDesc();
}
