package com.devision.subscription.service;

import com.devision.subscription.dto.SearchProfileRequest;
import com.devision.subscription.dto.SearchProfileResponse;
import com.devision.subscription.enums.EmploymentStatus;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.SearchProfile;
import com.devision.subscription.repository.SearchProfileRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchProfileServiceImpl implements SearchProfileService {

    private final SearchProfileRepository searchProfileRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SearchProfileServiceImpl(SearchProfileRepository searchProfileRepository,
            SubscriptionRepository subscriptionRepository) {
        this.searchProfileRepository = searchProfileRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public SearchProfileResponse upsert(String applicantId, SearchProfileRequest request) {
        // Require PREMIUM active subscription
        subscriptionRepository.findByApplicantIdAndIsActiveTrue(applicantId)
                .filter(sub -> sub.getPlanType() == PlanType.PREMIUM)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Premium plan required"));

        // Normalize tags
        List<String> tags = Optional.ofNullable(request.technicalTags)
                .orElseGet(ArrayList::new)
                .stream()
                .map(s -> s == null ? null : s.trim())
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        // Parse employment statuses from strings to enum
        Set<EmploymentStatus> statuses = Optional.ofNullable(request.employmentStatuses)
                .orElseGet(ArrayList::new)
                .stream()
                .map(s -> s == null ? null : s.trim().toUpperCase(Locale.ROOT))
                .filter(Objects::nonNull)
                .map(s -> {
                    try {
                        return EmploymentStatus.valueOf(s.replace('-', '_'));
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(EmploymentStatus.class)));

        // If neither FULL_TIME nor PART_TIME specified, include both
        if (!statuses.contains(EmploymentStatus.FULL_TIME) && !statuses.contains(EmploymentStatus.PART_TIME)) {
            statuses.add(EmploymentStatus.FULL_TIME);
            statuses.add(EmploymentStatus.PART_TIME);
        }

        // Salary defaults
        Integer minSalary = request.minSalary == null ? 0 : Math.max(0, request.minSalary);
        Integer maxSalary = request.maxSalary; // null means no upper limit

        // Parse job titles from semicolon-separated string
        List<String> titles = Optional.ofNullable(request.jobTitles)
                .map(str -> Arrays.stream(str.split(";"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .distinct()
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);

        // Upsert existing profile
        SearchProfile profile = searchProfileRepository.findByApplicantId(applicantId)
                .orElseGet(SearchProfile::new);

        profile.setApplicantId(applicantId);
        profile.setTechnicalTags(tags);
        profile.setEmploymentStatuses(new ArrayList<>(statuses));
        profile.setCountry(request.country);
        profile.setMinSalary(minSalary);
        profile.setMaxSalary(maxSalary);
        profile.setDesiredJobTitles(titles);

        searchProfileRepository.save(profile);

        return new SearchProfileResponse(
                applicantId,
                profile.getTechnicalTags(),
                profile.getEmploymentStatuses(),
                profile.getCountry(),
                profile.getMinSalary(),
                profile.getMaxSalary(),
                profile.getDesiredJobTitles());
    }

    @Override
    public SearchProfileResponse get(String applicantId) {
        return searchProfileRepository.findByApplicantId(applicantId)
                .map(p -> new SearchProfileResponse(
                        applicantId,
                        p.getTechnicalTags(),
                        p.getEmploymentStatuses(),
                        p.getCountry(),
                        p.getMinSalary(),
                        p.getMaxSalary(),
                        p.getDesiredJobTitles()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Search profile not found"));
    }
}
