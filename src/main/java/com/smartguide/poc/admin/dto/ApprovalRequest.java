package com.smartguide.poc.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {
    private List<Long> productIds;
    private String reviewedBy;
    private String reviewNotes;
}
