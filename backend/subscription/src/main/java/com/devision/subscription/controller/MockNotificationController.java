package com.devision.subscription.controller;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.model.Notification;
import com.devision.subscription.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class MockNotificationController {

    private final NotificationService notificationService;

    public MockNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Mock endpoint to simulate a job post event and trigger notifications
    @PostMapping("/mock/job-post")
    public ResponseEntity<Void> mockJobPost(@RequestBody JobPostEventDTO event) {
        notificationService.evaluateAndNotify(event);
        return ResponseEntity.ok().build();
    }

    // Fetch notifications for an applicant (for verification / frontend
    // integration)
    @GetMapping("/notifications/{applicantId}")
    public ResponseEntity<List<Notification>> list(@PathVariable String applicantId) {
        return ResponseEntity.ok(notificationService.listForApplicant(applicantId));
    }
}
