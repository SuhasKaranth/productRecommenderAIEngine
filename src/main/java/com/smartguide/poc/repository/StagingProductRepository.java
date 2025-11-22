package com.smartguide.poc.repository;

import com.smartguide.poc.entity.ScrapeLog;
import com.smartguide.poc.entity.StagingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StagingProduct entity
 */
@Repository
public interface StagingProductRepository extends JpaRepository<StagingProduct, Long> {

    List<StagingProduct> findByScrapeLog(ScrapeLog scrapeLog);

    List<StagingProduct> findByApprovalStatus(StagingProduct.ApprovalStatus status);

    List<StagingProduct> findBySourceWebsiteIdAndApprovalStatus(
            String websiteId,
            StagingProduct.ApprovalStatus status
    );

    @Query("SELECT sp FROM StagingProduct sp WHERE sp.approvalStatus = 'PENDING' ORDER BY sp.createdAt DESC")
    List<StagingProduct> findAllPendingOrderByCreatedAtDesc();

    @Query("SELECT sp FROM StagingProduct sp ORDER BY sp.createdAt DESC")
    List<StagingProduct> findAllOrderByCreatedAtDesc();

    long countByApprovalStatus(StagingProduct.ApprovalStatus status);

    void deleteByScrapeLog(ScrapeLog scrapeLog);
}
