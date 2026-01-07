package com.devision.subscription.service;

import com.devision.subscription.dto.SearchProfileRequest;
import com.devision.subscription.dto.SearchProfileResponse;

public interface SearchProfileService {
    SearchProfileResponse upsert(String applicantId, SearchProfileRequest request);

    SearchProfileResponse get(String applicantId);
}
