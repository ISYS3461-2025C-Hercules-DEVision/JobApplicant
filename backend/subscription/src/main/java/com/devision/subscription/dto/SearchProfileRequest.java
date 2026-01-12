package com.devision.subscription.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request shape for creating/updating a Search Profile via REST.
 */
public class SearchProfileRequest {
    public List<String> technicalTags;
    public List<String> employmentStatuses; // strings of enum names
    public String country;
    public BigDecimal minSalary;
    public BigDecimal maxSalary;
    public String jobTitles; // semicolon-separated
}
