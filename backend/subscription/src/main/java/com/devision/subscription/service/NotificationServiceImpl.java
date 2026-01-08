package com.devision.subscription.service;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.Notification;
import com.devision.subscription.model.SearchProfile;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.NotificationRepository;
import com.devision.subscription.repository.SearchProfileRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates incoming job-post events against stored Search Profiles and
 * persists notifications for applicants with active PREMIUM plans when a
 * basic match (skills intersection and country equality) is found.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final SearchProfileRepository searchProfileRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(SearchProfileRepository searchProfileRepository,
            SubscriptionRepository subscriptionRepository,
            NotificationRepository notificationRepository) {
        this.searchProfileRepository = searchProfileRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void evaluateAndNotify(JobPostEventDTO event) {
        List<SearchProfile> profiles = searchProfileRepository.findAll();
        Set<String> eventSkills = normalize(event.getSkills());
        String eventCountry = safeLower(event.getCountry());

        for (SearchProfile profile : profiles) {
            String applicantId = profile.getApplicantId();
            Optional<Subscription> active = subscriptionRepository.findByApplicantIdAndIsActiveTrue(applicantId);
            if (active.isEmpty() || active.get().getPlanType() != PlanType.PREMIUM) {
                continue; // only active premium subscribers receive notifications
            }

            Set<String> profileTags = normalize(profile.getTechnicalTags());
            String profileCountry = safeLower(profile.getCountry());

            // Basic match: intersect skills and country match
            Set<String> matchedSkills = new HashSet<>(profileTags);
            matchedSkills.retainAll(eventSkills);

            if (!matchedSkills.isEmpty() && Objects.equals(eventCountry, profileCountry)) {
                Notification n = new Notification();
                n.setApplicantId(applicantId);
                n.setJobId(event.getJobId());
                n.setJobTitle(event.getTitle());
                n.setCompany(event.getCompany());
                n.setMatchedSkills(new ArrayList<>(matchedSkills));
                n.setCountry(event.getCountry());
                n.setMatchedAt(Instant.now());
                notificationRepository.save(n);
            }
        }
    }

    @Override
    public List<Notification> listForApplicant(String applicantId) {
        return notificationRepository.findByApplicantIdOrderByMatchedAtDesc(applicantId);
    }

    private Set<String> normalize(List<String> items) {
        if (items == null)
            return Collections.emptySet();
        return items.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String safeLower(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }
}
