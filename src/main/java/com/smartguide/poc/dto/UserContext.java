package com.smartguide.poc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User context for personalization
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {
    private List<String> currentProducts = new ArrayList<>();
    private BigDecimal minIncome;
    private Integer creditScore;
    private Integer age;
}
