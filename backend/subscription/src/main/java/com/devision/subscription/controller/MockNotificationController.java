package com.devision.subscription.controller;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.model.Notification;
import com.devision.subscription.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Mock endpoints to simulate job-post updates and inspect generated
 * notifications for an applicant. Useful for local/dev verification and
 * frontend integration prior to enabling Kafka consumers.
 */
@RestController
@RequestMapping("/api/v1/subscriptions")
public class MockNotificationController {

    private final NotificationService notificationService;

    public MockNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
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
}
