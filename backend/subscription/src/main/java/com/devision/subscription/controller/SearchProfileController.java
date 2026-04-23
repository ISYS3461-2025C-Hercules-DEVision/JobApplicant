package com.devision.subscription.controller; // Controller package for subscription APIs

import com.devision.subscription.dto.SearchProfileRequest; // Request DTO for search profile
import com.devision.subscription.dto.SearchProfileResponse; // Response DTO for search profile
import com.devision.subscription.service.SearchProfileService; // Service interface
import org.springframework.web.bind.annotation.*; // Spring MVC annotations

/**
 * REST endpoints for managing an applicant's Search Profile used for
 * job-match notifications. Requires an active PREMIUM plan to upsert.
 */
@RestController // Marks class as REST controller
@RequestMapping("/api/v1/subscriptions") // Base path for subscription endpoints
public class SearchProfileController { // Search profile REST controller

    private final SearchProfileService searchProfileService; // Service dependency

    public SearchProfileController(SearchProfileService searchProfileService) { // Constructor injection
        this.searchProfileService = searchProfileService; // Assign service
    }

    /**
     * Creates or updates the Search Profile for the applicant.
     * Enforced by service to require an active PREMIUM subscription.
     */
    @PostMapping("/{applicantId}/search-profile") // Create or update search profile
    public SearchProfileResponse upsert( // Upsert endpoint
            @PathVariable String applicantId, // Applicant identifier
            @RequestBody SearchProfileRequest request) { // Request payload
        return searchProfileService.upsert(applicantId, request); // Delegate to service
    }

    /**
     * Retrieves the applicant's Search Profile. Returns a safe default when
     * no profile is found so clients can render without special-casing.
     */
    @GetMapping("/{applicantId}/search-profile") // Fetch search profile
    public SearchProfileResponse get(@PathVariable String applicantId) { // Get endpoint
        return searchProfileService.get(applicantId); // Delegate to service
    }
}
