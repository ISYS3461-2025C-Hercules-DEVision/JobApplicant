package com.devision.subscription.dto; // DTO package

import java.math.BigDecimal; // Salary values
import java.util.List; // Lists of values

/**
 * Request shape for creating/updating a Search Profile via REST.
 */
public class SearchProfileRequest { // Request payload for search profile
    public List<String> technicalTags; // Requested technical tags
    public List<String> employmentStatuses; // Strings of enum names
    public String country; // Desired country
    public BigDecimal minSalary; // Minimum salary
    public BigDecimal maxSalary; // Maximum salary
    public String jobTitles; // Semicolon-separated job titles
} // End DTO
