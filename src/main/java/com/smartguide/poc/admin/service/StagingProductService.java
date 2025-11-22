package com.smartguide.poc.admin.service;

import com.smartguide.poc.admin.dto.StagingProductDTO;
import com.smartguide.poc.entity.Product;
import com.smartguide.poc.entity.StagingProduct;
import com.smartguide.poc.repository.ProductRepository;
import com.smartguide.poc.repository.StagingProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing staging products and approval workflow
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StagingProductService {

    private final StagingProductRepository stagingProductRepository;
    private final ProductRepository productRepository;

    /**
     * Get all pending staging products
     */
    public List<StagingProductDTO> getAllPendingProducts() {
        return stagingProductRepository.findAllPendingOrderByCreatedAtDesc()
                .stream()
                .map(StagingProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all staging products
     */
    public List<StagingProductDTO> getAllStagingProducts() {
        return stagingProductRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(StagingProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get staging product by ID
     */
    public StagingProductDTO getStagingProductById(Long id) {
        return stagingProductRepository.findById(id)
                .map(StagingProductDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Staging product not found: " + id));
    }

    /**
     * Update staging product
     */
    @Transactional
    public StagingProductDTO updateStagingProduct(Long id, StagingProductDTO dto) {
        StagingProduct product = stagingProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staging product not found: " + id));

        // Update fields
        product.setProductCode(dto.getProductCode());
        product.setProductName(dto.getProductName());
        product.setCategory(dto.getCategory());
        product.setSubCategory(dto.getSubCategory());
        product.setDescription(dto.getDescription());
        product.setIslamicStructure(dto.getIslamicStructure());
        product.setAnnualRate(dto.getAnnualRate());
        product.setAnnualFee(dto.getAnnualFee());
        product.setMinIncome(dto.getMinIncome());
        product.setMinCreditScore(dto.getMinCreditScore());
        product.setEligibilityCriteria(dto.getEligibilityCriteria());
        product.setKeyBenefits(dto.getKeyBenefits());
        product.setShariaCertified(dto.getShariaCertified());
        product.setActive(dto.getActive());

        StagingProduct saved = stagingProductRepository.save(product);
        log.info("Updated staging product: {}", id);
        return StagingProductDTO.fromEntity(saved);
    }

    /**
     * Approve single staging product and move to production
     */
    @Transactional
    public void approveStagingProduct(Long id, String reviewedBy, String reviewNotes) {
        StagingProduct stagingProduct = stagingProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staging product not found: " + id));

        // Create or update product in production table
        Product product = productRepository.findByProductCode(stagingProduct.getProductCode())
                .orElse(new Product());

        copyToProduct(stagingProduct, product);

        productRepository.save(product);

        // Update staging product status
        stagingProduct.setApprovalStatus(StagingProduct.ApprovalStatus.APPROVED);
        stagingProduct.setReviewedBy(reviewedBy);
        stagingProduct.setReviewedAt(LocalDateTime.now());
        stagingProduct.setReviewNotes(reviewNotes);
        stagingProductRepository.save(stagingProduct);

        log.info("Approved staging product {} and moved to production", id);
    }

    /**
     * Bulk approve multiple staging products
     */
    @Transactional
    public void bulkApproveProducts(List<Long> productIds, String reviewedBy, String reviewNotes) {
        for (Long id : productIds) {
            try {
                approveStagingProduct(id, reviewedBy, reviewNotes);
            } catch (Exception e) {
                log.error("Failed to approve product {}: {}", id, e.getMessage());
            }
        }
    }

    /**
     * Reject/delete staging product
     */
    @Transactional
    public void rejectStagingProduct(Long id, String reviewedBy, String reviewNotes) {
        StagingProduct stagingProduct = stagingProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staging product not found: " + id));

        stagingProduct.setApprovalStatus(StagingProduct.ApprovalStatus.REJECTED);
        stagingProduct.setReviewedBy(reviewedBy);
        stagingProduct.setReviewedAt(LocalDateTime.now());
        stagingProduct.setReviewNotes(reviewNotes);
        stagingProductRepository.save(stagingProduct);

        log.info("Rejected staging product: {}", id);
    }

    /**
     * Delete staging product
     */
    @Transactional
    public void deleteStagingProduct(Long id) {
        stagingProductRepository.deleteById(id);
        log.info("Deleted staging product: {}", id);
    }

    /**
     * Get counts by status
     */
    public long getPendingCount() {
        return stagingProductRepository.countByApprovalStatus(StagingProduct.ApprovalStatus.PENDING);
    }

    public long getApprovedCount() {
        return stagingProductRepository.countByApprovalStatus(StagingProduct.ApprovalStatus.APPROVED);
    }

    public long getRejectedCount() {
        return stagingProductRepository.countByApprovalStatus(StagingProduct.ApprovalStatus.REJECTED);
    }

    /**
     * Copy data from staging product to production product
     */
    private void copyToProduct(StagingProduct staging, Product product) {
        product.setProductCode(staging.getProductCode());
        product.setProductName(staging.getProductName());
        product.setCategory(staging.getCategory());
        product.setSubCategory(staging.getSubCategory());
        product.setDescription(staging.getDescription());
        product.setIslamicStructure(staging.getIslamicStructure());
        product.setAnnualRate(staging.getAnnualRate());
        product.setAnnualFee(staging.getAnnualFee());
        product.setMinIncome(staging.getMinIncome());
        product.setMinCreditScore(staging.getMinCreditScore());
        product.setEligibilityCriteria(staging.getEligibilityCriteria());
        product.setKeyBenefits(staging.getKeyBenefits());
        product.setShariaCertified(staging.getShariaCertified());
        product.setActive(staging.getActive());
        product.setSourceWebsiteId(staging.getSourceWebsiteId());
        product.setSourceUrl(staging.getSourceUrl());
        product.setScrapedAt(staging.getScrapedAt());
        product.setDataQualityScore(staging.getDataQualityScore());
    }
}
