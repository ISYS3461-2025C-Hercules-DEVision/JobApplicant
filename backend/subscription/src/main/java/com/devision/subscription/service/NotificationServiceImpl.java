package com.devision.subscription.service;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.enums.PlanType;
import com.devision.subscription.model.Notification;
import com.devision.subscription.model.SearchProfile;
import com.devision.subscription.model.Subscription;
import com.devision.subscription.repository.NotificationRepository;
import com.devision.subscription.repository.SearchProfileRepository;
import com.devision.subscription.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

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
        String eventTitle = safeLower(event.getTitle());

        for (SearchProfile profile : profiles) {
            String applicantId = profile.getApplicantId();
            Optional<Subscription> active = subscriptionRepository.findByApplicantIdAndIsActiveTrue(applicantId);
            if (active.isEmpty() || active.get().getPlanType() != PlanType.PREMIUM) {
                continue; // only active premium subscribers receive notifications
            }

            Set<String> profileTags = normalize(profile.getTechnicalTags());
            String profileCountry = safeLower(profile.getCountry());
            Set<String> desiredTitles = normalize(profile.getDesiredJobTitles());

            // Matches
            Set<String> matchedSkills = new HashSet<>(profileTags);
            matchedSkills.retainAll(eventSkills);

            boolean titleMatch = false;
            if (eventTitle != null && !desiredTitles.isEmpty()) {
                for (String t : desiredTitles) {
                    if (t != null && !t.isBlank() && eventTitle.contains(t)) { titleMatch = true; break; }
                }
            }

            // Country: if profile sets a country, require equality; otherwise ignore
            boolean countryMatch = (profileCountry == null) || Objects.equals(eventCountry, profileCountry);

            // Relaxed rule: at least one of (skills intersect OR job-title contains desired) must match
            boolean coreMatch = (!matchedSkills.isEmpty()) || titleMatch;

            if (coreMatch && countryMatch) {
                Notification n = new Notification();
                n.setApplicantId(applicantId);
                n.setJobId(event.getJobId());
                n.setJobTitle(event.getTitle());
                n.setCompany(event.getCompany());
                n.setMatchedSkills(new ArrayList<>(matchedSkills));
                n.setCountry(event.getCountry());
                n.setMatchedAt(Instant.now());
                notificationRepository.save(n);
                log.info("Notification saved: applicant={} jobId={} titleMatch={} skillsMatch={} countryMatch={}",
                        applicantId, event.getJobId(), titleMatch, !matchedSkills.isEmpty(), countryMatch);
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
