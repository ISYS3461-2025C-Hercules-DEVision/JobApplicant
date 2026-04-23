package com.devision.subscription.service; // Service package

import com.devision.subscription.dto.SearchProfileRequest; // Request DTO
import com.devision.subscription.dto.SearchProfileResponse; // Response DTO

public interface SearchProfileService { // Interface for search profile operations
    SearchProfileResponse upsert(String applicantId, SearchProfileRequest request); // Create or update profile

    SearchProfileResponse get(String applicantId); // Get existing profile or defaults
} // End interface
