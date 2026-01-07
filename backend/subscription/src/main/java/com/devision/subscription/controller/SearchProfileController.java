package com.devision.subscription.controller;

import com.devision.subscription.dto.SearchProfileRequest;
import com.devision.subscription.dto.SearchProfileResponse;
import com.devision.subscription.service.SearchProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SearchProfileController {

    private final SearchProfileService searchProfileService;

    public SearchProfileController(SearchProfileService searchProfileService) {
        this.searchProfileService = searchProfileService;
    }

    @PostMapping("/{applicantId}/search-profile")
    public SearchProfileResponse upsert(
            @PathVariable String applicantId,
            @RequestBody SearchProfileRequest request) {
        return searchProfileService.upsert(applicantId, request);
    }

    @GetMapping("/{applicantId}/search-profile")
    public SearchProfileResponse get(@PathVariable String applicantId) {
        return searchProfileService.get(applicantId);
    }
}
