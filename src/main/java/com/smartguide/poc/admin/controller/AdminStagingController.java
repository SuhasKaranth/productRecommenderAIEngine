package com.smartguide.poc.admin.controller;

import com.smartguide.poc.admin.dto.ApprovalRequest;
import com.smartguide.poc.admin.dto.StagingProductDTO;
import com.smartguide.poc.admin.service.StagingProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin controller for managing staging products and approval workflow
 */
@RestController
@RequestMapping("/api/admin/staging")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Staging Products", description = "Manage staging products and approval workflow")
@CrossOrigin(origins = "*")
public class AdminStagingController {

    private final StagingProductService stagingProductService;

    @GetMapping
    @Operation(summary = "Get all staging products")
    public ResponseEntity<List<StagingProductDTO>> getAllStagingProducts(
            @RequestParam(required = false, defaultValue = "false") boolean pendingOnly) {
        List<StagingProductDTO> products = pendingOnly
                ? stagingProductService.getAllPendingProducts()
                : stagingProductService.getAllStagingProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get staging product by ID")
    public ResponseEntity<StagingProductDTO> getStagingProduct(@PathVariable Long id) {
        StagingProductDTO product = stagingProductService.getStagingProductById(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update staging product")
    public ResponseEntity<StagingProductDTO> updateStagingProduct(
            @PathVariable Long id,
            @RequestBody StagingProductDTO dto) {
        StagingProductDTO updated = stagingProductService.updateStagingProduct(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve single staging product")
    public ResponseEntity<Map<String, Object>> approveStagingProduct(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalRequest request) {
        String reviewedBy = request != null ? request.getReviewedBy() : "admin";
        String reviewNotes = request != null ? request.getReviewNotes() : null;

        stagingProductService.approveStagingProduct(id, reviewedBy, reviewNotes);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product approved and moved to production");
        response.put("productId", id);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/bulk-approve")
    @Operation(summary = "Bulk approve multiple staging products")
    public ResponseEntity<Map<String, Object>> bulkApproveProducts(
            @RequestBody ApprovalRequest request) {
        stagingProductService.bulkApproveProducts(
                request.getProductIds(),
                request.getReviewedBy() != null ? request.getReviewedBy() : "admin",
                request.getReviewNotes()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Products approved successfully");
        response.put("count", request.getProductIds().size());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject staging product")
    public ResponseEntity<Map<String, Object>> rejectStagingProduct(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalRequest request) {
        String reviewedBy = request != null ? request.getReviewedBy() : "admin";
        String reviewNotes = request != null ? request.getReviewNotes() : null;

        stagingProductService.rejectStagingProduct(id, reviewedBy, reviewNotes);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product rejected");
        response.put("productId", id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete staging product")
    public ResponseEntity<Map<String, Object>> deleteStagingProduct(@PathVariable Long id) {
        stagingProductService.deleteStagingProduct(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product deleted");
        response.put("productId", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get staging product statistics")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", stagingProductService.getPendingCount());
        stats.put("approved", stagingProductService.getApprovedCount());
        stats.put("rejected", stagingProductService.getRejectedCount());

        return ResponseEntity.ok(stats);
    }
}
