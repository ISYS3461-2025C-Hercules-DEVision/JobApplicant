package com.devision.subscription.controller;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.model.Notification;
import com.devision.subscription.dto.SearchProfileRequest;
import com.devision.subscription.dto.SearchProfileResponse;
import com.devision.subscription.service.SearchProfileService;
import com.devision.subscription.service.SubscriptionService;
import com.devision.subscription.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mock endpoints to simulate job-post updates and inspect generated
 * notifications for an applicant. Useful for local/dev verification and
 * frontend integration prior to enabling Kafka consumers.
 */
@RestController
@Profile("dev")
@RequestMapping("/api/v1/subscriptions")
public class MockNotificationController {

    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;
    private final SearchProfileService searchProfileService;

    public MockNotificationController(NotificationService notificationService,
            SubscriptionService subscriptionService,
            SearchProfileService searchProfileService) {
        this.notificationService = notificationService;
        this.subscriptionService = subscriptionService;
        this.searchProfileService = searchProfileService;
    }

    /**
     * Simulates a Job Manager job-post event, triggering evaluation against
     * stored Search Profiles and persisting notifications for matched users.
     */
    @PostMapping("/mock/job-post")
    public ResponseEntity<Void> mockJobPost(@RequestBody JobPostEventDTO event) {
        notificationService.evaluateAndNotify(event);
        return ResponseEntity.ok().build();
    }

    /**
     * Returns notifications for a given applicant, most-recent first.
     */
    @GetMapping("/notifications/{applicantId}")
    public ResponseEntity<List<Notification>> list(@PathVariable String applicantId) {
        return ResponseEntity.ok(notificationService.listForApplicant(applicantId));
    }

    /**
     * Seeds demo data for a given applicant to showcase notifications end-to-end.
     * Steps:
     * - Ensures PREMIUM subscription via mock checkout
     * - Upserts a simple Search Profile (tags + country)
     * - Emits N mock job posts that should match and create notifications
     * Returns: the current notification list for the applicant.
     */
    @PostMapping("/mock/seed/{applicantId}")
    public ResponseEntity<List<Notification>> seedDemo(
            @PathVariable String applicantId,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "count", required = false, defaultValue = "3") int count,
            @RequestParam(name = "country", required = false, defaultValue = "US") String country) {
        // 1) Ensure PREMIUM subscription (initiate payment; auth not required here)
        subscriptionService.createMockPayment(applicantId, email, null);

        // 2) Upsert a basic search profile
        SearchProfileRequest req = new SearchProfileRequest();
        req.technicalTags = java.util.List.of("java", "spring", "kafka");
        req.employmentStatuses = java.util.List.of("full-time");
        req.country = country;
        req.minSalary = BigDecimal.ZERO;
        req.maxSalary = null;
        req.jobTitles = "Engineer;Developer";
        SearchProfileResponse profile = searchProfileService.upsert(applicantId, req);

        // 3) Emit N mock job posts (should match by skills + country)
        for (int i = 1; i <= Math.max(1, count); i++) {
            JobPostEventDTO event = new JobPostEventDTO();
            event.setJobId("demo-job-" + i);
            event.setTitle("Backend Engineer " + i);
            event.setCompany("Demo Corp " + i);
            event.setSkills(java.util.List.of("java", "docker", "kafka"));
            event.setCountry(country);
            notificationService.evaluateAndNotify(event);
        }

        // 4) Return notifications for the applicant
        return ResponseEntity.ok(notificationService.listForApplicant(applicantId));
    }
}
