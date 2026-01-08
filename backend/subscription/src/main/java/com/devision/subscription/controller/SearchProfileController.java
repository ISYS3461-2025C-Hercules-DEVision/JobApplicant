package com.devision.subscription.controller;

import com.devision.subscription.dto.SearchProfileRequest;
import com.devision.subscription.dto.SearchProfileResponse;
import com.devision.subscription.service.SearchProfileService;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for managing an applicant's Search Profile used for
 * job-match notifications. Requires an active PREMIUM plan to upsert.
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SearchProfileController {

    private final SearchProfileService searchProfileService;

    public SearchProfileController(SearchProfileService searchProfileService) {
        this.searchProfileService = searchProfileService;
    }

    /**
     * Creates or updates the Search Profile for the applicant.
     * Enforced by service to require an active PREMIUM subscription.
     */
    @PostMapping("/{applicantId}/search-profile")
    public SearchProfileResponse upsert(
            @PathVariable String applicantId,
            @RequestBody SearchProfileRequest request) {
        return searchProfileService.upsert(applicantId, request);
    }

    /**
     * Retrieves the applicant's Search Profile. Returns a safe default when
     * no profile is found so clients can render without special-casing.
     */
    @GetMapping("/{applicantId}/search-profile")
    public SearchProfileResponse get(@PathVariable String applicantId) {
        return searchProfileService.get(applicantId);
    }
}
