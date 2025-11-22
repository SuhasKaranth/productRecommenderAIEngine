package com.smartguide.poc.repository;

import com.smartguide.poc.entity.ScrapeSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ScrapeSource entity
 */
@Repository
public interface ScrapeSourceRepository extends JpaRepository<ScrapeSource, Long> {

    Optional<ScrapeSource> findByWebsiteId(String websiteId);

    List<ScrapeSource> findByActiveTrue();

    boolean existsByWebsiteId(String websiteId);
}
